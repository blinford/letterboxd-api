package io.github.blinford.letterboxd;

import io.github.blinford.letterboxd.reader.LetterboxdFilmReader;
import io.github.blinford.letterboxd.reader.LetterboxdUserReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LetterboxdAppTests {

    @Autowired
    private LetterboxdUserReader letterboxdUserReader;
    @Autowired
    private LetterboxdFilmReader letterboxdFilmReader;
}
