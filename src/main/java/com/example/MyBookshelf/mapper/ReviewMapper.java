package com.example.MyBookshelf.mapper;

import com.example.MyBookshelf.dto.request.ReviewCreateDto;
import com.example.MyBookshelf.dto.responce.ReviewResponseDto;
import com.example.MyBookshelf.entity.ReviewEntity;

public class ReviewMapper {

    public static ReviewResponseDto toResponseDto(ReviewEntity reviewEntity) {
        return ReviewResponseDto.builder()
                .username(reviewEntity.getUser().getUsername())
                .comment(reviewEntity.getComment())
                .rating(reviewEntity.getRating())
                .build();
    }

    public static ReviewEntity fromRequestDto(ReviewCreateDto dto) {
        return ReviewEntity.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();
    }

}

