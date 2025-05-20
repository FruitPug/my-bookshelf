package com.example.MyBookshelf;

import org.springframework.boot.SpringApplication;

public class TestMyBookshelfApplication {

	public static void main(String[] args) {
		SpringApplication.from(MyBookshelfApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
