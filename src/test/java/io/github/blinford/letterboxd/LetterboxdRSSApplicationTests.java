package io.github.blinford.letterboxd;

import io.github.blinford.letterboxd.reader.LetterboxdRSSReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LetterboxdRSSApplicationTests {

	@Autowired
	private LetterboxdRSSReader reader;

	@Test
	void contextLoads() {
	}
}
