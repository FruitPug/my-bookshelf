package com.example.MyBookshelf.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private Long userId;
    private String comment;
    private double rating;
}
