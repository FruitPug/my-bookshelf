package com.example.MyBookshelf.dto.responce;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookResponseDto {
    private Long id;
    private String title;
    private String author;
    private String genre;
    private String status;
    private double rating;
    private List<ReviewResponseDto> reviews;
    private int reviewCount;
}

