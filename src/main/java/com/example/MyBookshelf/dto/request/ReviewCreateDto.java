package com.example.MyBookshelf.dto.request;

import lombok.Data;

@Data

public class ReviewCreateDto {
    private Long userId;
    private String comment;
    private double rating;
}
