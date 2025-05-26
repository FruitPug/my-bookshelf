package com.example.MyBookshelf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MyBookshelfApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyBookshelfApplication.class, args);
	}

}
