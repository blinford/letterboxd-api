package io.github.blinford.letterboxd.reader;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.blinford.letterboxd.reader.LetterboxdReader.DataType.ATTRIBUTE;

@Component
public class LetterboxdUserReader {

    private final LetterboxdReader letterboxdReader;

    // https://letterboxd.com/{username}

    private static final String USER_URL = "/%s";
    private static final String USER_FILMS_URL = USER_URL + "/films/page/%%s";
    private static final String USER_FOLLOWING_URL = USER_URL + "/following";
    private static final String USER_FOLLOWERS_URL = USER_URL + "/followers";

    public LetterboxdUserReader(LetterboxdReader letterboxdReader) {
        this.letterboxdReader = letterboxdReader;
    }

    public boolean isValidUsername(String username) {
        try {
            letterboxdReader.getDocument(String.format(USER_URL, username));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private Set<String> getData(String username, String relativeURL, String select, String attribute) {
        return letterboxdReader.getData(ATTRIBUTE, String.format(relativeURL, username), select, attribute, null);
    }

    public Set<String> getFilms(String username) {
        return getData(username, USER_FILMS_URL, "div.linked-film-poster", "data-film-slug");
    }

    public Set<String> getFollowing(String username) {
        return getData(username, USER_FOLLOWING_URL, "a.name", "href")
                .stream().map(str -> str.replace("/", "")).collect(Collectors.toSet());
    }

    public Set<String> getFollowers(String username) {
        return getData(username, USER_FOLLOWERS_URL, "a.name", "href")
                .stream().map(str -> str.replace("/", "")).collect(Collectors.toSet());
    }

    public Set<String> getFriends(String username) {

        Set<String> friends = new HashSet<>(),
                following = getFollowing(username),
                followers = getFollowers(username);

        if(following.size() < followers.size()) {
            for(String acc : following) {
                if(followers.contains(acc)) {
                    friends.add(acc);
                }
            }
        }
        else {
            for(String acc : followers) {
                if(following.contains(acc)) {
                    friends.add(acc);
                }
            }
        }

        return friends;
    }
}
