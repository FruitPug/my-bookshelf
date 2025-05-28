package com.example.MyBookshelf.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookCreateDto {
    @NotBlank(message="Title is required")
    private String title;

    @NotBlank(message="Author is required")
    private String author;

    @NotBlank(message="Genre is required")
    private String genre;
}
