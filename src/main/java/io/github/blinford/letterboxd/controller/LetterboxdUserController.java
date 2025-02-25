package io.github.blinford.letterboxd.controller;

import io.github.blinford.letterboxd.reader.LetterboxdUserReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@CrossOrigin
public class LetterboxdUserController {

    private final LetterboxdUserReader letterboxdUserReader;

    public LetterboxdUserController(LetterboxdUserReader letterboxdUserReader) {
        this.letterboxdUserReader = letterboxdUserReader;
    }

    private String getUsernameNotFoundMessage(String username) {
        return String.format("username %s not found", username);
    }

    public record UserFilmsResponse(String username, Integer numFilms, Set<String> films, String error) {}
    @GetMapping("/user/{username}/films")
    public ResponseEntity<UserFilmsResponse> getUserFilms(@PathVariable String username) {
        if(letterboxdUserReader.isValidUsername(username)) {
            Set<String> films = letterboxdUserReader.getFilms(username);
            return new ResponseEntity<>(new UserFilmsResponse(username, films.size(), films, null), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new UserFilmsResponse(username, null, null, getUsernameNotFoundMessage(username)), HttpStatus.NOT_FOUND);
        }
    }

    public record UserFollowingResponse(String username, Set<String> following, String error) {}
    @GetMapping("/user/{username}/following")
    public ResponseEntity<UserFollowingResponse> getUserFollowing(@PathVariable String username) {
        if(letterboxdUserReader.isValidUsername(username)) {
            return new ResponseEntity<>(new UserFollowingResponse(username, letterboxdUserReader.getFollowing(username), null), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new UserFollowingResponse(username, null, getUsernameNotFoundMessage(username)), HttpStatus.NOT_FOUND);
        }
    }

    public record UserFollowersResponse(String username, Set<String> followers, String error) {}
    @GetMapping("/user/{username}/followers")
    public ResponseEntity<UserFollowersResponse> getUserFollowers(@PathVariable String username) {
        if(letterboxdUserReader.isValidUsername(username)) {
            return new ResponseEntity<>(new UserFollowersResponse(username, letterboxdUserReader.getFollowers(username), null), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new UserFollowersResponse(username, null, getUsernameNotFoundMessage(username)), HttpStatus.NOT_FOUND);
        }
    }
}