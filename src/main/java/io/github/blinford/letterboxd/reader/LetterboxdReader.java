package io.github.blinford.letterboxd.reader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

@Component
public class LetterboxdReader {

    public enum DataType {
        REVIEW, ATTRIBUTE
    }

    Logger log = LoggerFactory.getLogger(LetterboxdReader.class);

    private static final String BASE_URL = "https://letterboxd.com",
            SPOILER_WARNING = "This review may contain spoilers. I can handle the truth. ";

    public Document getDocument(String relativeUrl) throws IOException {
        log.debug(relativeUrl);
        return Jsoup.connect(BASE_URL + relativeUrl).get();
    }

    public Set<String> readDocument(DataType dataType, String relativeUrl, String select, String attribute) throws IOException {
        Document document = getDocument(relativeUrl);
        Set<String> result = new HashSet<>();
        switch(dataType) {
            case ATTRIBUTE -> {
                for (Element e : document.select(select)) {
                    result.add(e.attr(attribute));
                }
            }
            case REVIEW -> {
                String curr;
                for (Element e : document.select(select)) {
                    curr = e.text();
                    if(!curr.startsWith("Letterboxd")) {
                        result.add(curr.startsWith(SPOILER_WARNING) ? curr.substring(SPOILER_WARNING.length()) : curr);
                    }
                }
            }
            default ->
                throw new IllegalArgumentException("invalid data type");
        }
        return result;
    }

    public Set<String> getData(DataType dataType, String relativeUrl, String select, String attribute, Integer pageLimit) {
        int numPages = getNumPages(String.format(relativeUrl, 1));
        if(pageLimit != null) {
            numPages = Math.min(numPages, pageLimit);
        }

        List<CompletableFuture<Set<String>>> pageFutures = new ArrayList<>();
        try(var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for(int page = 1; page <= numPages; page++) {
                String url = String.format(relativeUrl, page);
                pageFutures.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        return readDocument(dataType, url, select, attribute);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, executor));
            }
        }

        return awaitFutures(pageFutures);
    }

    private Set<String> awaitFutures(List<CompletableFuture<Set<String>>> pageFutures) {
        Set<String> result = new HashSet<>();
        for(CompletableFuture<Set<String>> future : pageFutures) {
            try {
                result.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("error processing future", e);
                Thread.currentThread().interrupt();
            }
        }
        return result;
    }

    private int getNumPages(String relativeUrl) {
        try {
            Elements elements = getDocument(relativeUrl).select("li.paginate-page");
            return elements.isEmpty() ? 10 : Integer.parseInt(elements.getLast().text());
        } catch (IOException e) {
            log.error("error getting number of pages", e);
            return 10;
        }
    }
}
