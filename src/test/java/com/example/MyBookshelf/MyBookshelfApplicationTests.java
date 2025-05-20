package com.example.MyBookshelf;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MyBookshelfApplicationTests {

	@Test
	void contextLoads() {
	}

}
