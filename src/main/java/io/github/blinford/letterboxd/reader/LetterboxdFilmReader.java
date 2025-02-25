package io.github.blinford.letterboxd.reader;

import org.springframework.stereotype.Component;

import java.util.*;

import static io.github.blinford.letterboxd.reader.LetterboxdReader.DataType.REVIEW;

@Component
public class LetterboxdFilmReader {

    private final LetterboxdReader letterboxdReader;

    // https://letterboxd.com/film/{film}

    private static final String FILM_URL = "/film/%s";
    private static final String RELATIVE_URL = FILM_URL + "/reviews/by/activity/page/%%s/";

    public LetterboxdFilmReader(LetterboxdReader letterboxdReader) {
        this.letterboxdReader = letterboxdReader;
    }

    public boolean isValidFilm(String film) {
        try {
            letterboxdReader.getDocument(String.format(FILM_URL, film));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Set<String> getReviews(String film) {
        return getReviews(film, 10);
    }

    public Set<String> getReviews(String film, Integer pageLimit) {
        return letterboxdReader.getData(REVIEW, String.format(RELATIVE_URL, film), "div.body-text", null, pageLimit);
    }
}
