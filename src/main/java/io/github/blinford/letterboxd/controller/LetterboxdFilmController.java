package io.github.blinford.letterboxd.controller;

import io.github.blinford.letterboxd.reader.LetterboxdFilmReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@CrossOrigin
public class LetterboxdFilmController {

    private final LetterboxdFilmReader letterboxdFilmReader;

    public LetterboxdFilmController(LetterboxdFilmReader letterboxdFilmReader) {
        this.letterboxdFilmReader = letterboxdFilmReader;
    }

    private String getFilmNotFoundMessage(String film) {
        return String.format("film %s not found", film);
    }

    public record FilmReviewsResponse(String film, Set<String> reviews, String error) {}
    @GetMapping("/film/{film}/reviews")
    public ResponseEntity<FilmReviewsResponse> getFilmReviews(@PathVariable String film) {
        if(letterboxdFilmReader.isValidFilm(film)) {
            return new ResponseEntity<>(new FilmReviewsResponse(film, letterboxdFilmReader.getReviews(film), null), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new FilmReviewsResponse(film, null, getFilmNotFoundMessage(film)), HttpStatus.NOT_FOUND);
        }
    }
}