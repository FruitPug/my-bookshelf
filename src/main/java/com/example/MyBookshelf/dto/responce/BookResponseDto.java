package com.example.MyBookshelf.dto.responce;

import com.example.MyBookshelf.entity.Review;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookResponseDto {
    private Long id;
    private String title;
    private String author;
    private String genre;
    private String status;
    private double rating;
    private List<ReviewResponseDto> reviews = new ArrayList<>();
    private int reviewCount;
}

