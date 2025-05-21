package com.example.MyBookshelf.dto.responce;

import lombok.Data;

@Data
public class ReviewResponseDto {
    private Long id;
    private Long bookId;
    private String username;
    private String comment;
    private double rating;
}

