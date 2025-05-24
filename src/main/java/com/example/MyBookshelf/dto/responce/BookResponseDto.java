package com.example.MyBookshelf.dto.responce;

import com.example.MyBookshelf.status.ReadingStatus;
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
    private ReadingStatus userStatus;
    private double rating;
    private int reviewCount;
    private List<ReviewResponseDto> reviews;
}

