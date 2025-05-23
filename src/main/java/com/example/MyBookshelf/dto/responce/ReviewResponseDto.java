package com.example.MyBookshelf.dto.responce;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewResponseDto {
    private String username;
    private String comment;
    private double rating;
}

