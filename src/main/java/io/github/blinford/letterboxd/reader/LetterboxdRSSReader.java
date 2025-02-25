package io.github.blinford.letterboxd.reader;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Component
public class LetterboxdRSSReader {

    private final SyndFeedInput input;

    public LetterboxdRSSReader() {
        input = new SyndFeedInput();
    }

    public List<SyndEntry> getEntries(String username) {
        try {
            return input.build(new XmlReader(new URL(String.format("https://letterboxd.com/%s/rss", username)))).getEntries();
        } catch (IOException | FeedException e) {
            return null;
        }
    }
}
