package com.example.MyBookshelf.dto.request;

import lombok.Data;

@Data
public class BookCreateDto {
    private String title;
    private String author;
    private String genre;
}
