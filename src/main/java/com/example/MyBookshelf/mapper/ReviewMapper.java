package com.example.MyBookshelf.mapper;

import com.example.MyBookshelf.dto.ReviewDto;
import com.example.MyBookshelf.dto.request.ReviewCreateDto;
import com.example.MyBookshelf.dto.responce.ReviewResponseDto;
import com.example.MyBookshelf.entity.ReviewEntity;

public class ReviewMapper {

    public static ReviewResponseDto toResponseDto(ReviewEntity reviewEntity) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(reviewEntity.getId());
        dto.setBookId(reviewEntity.getBookId());
        dto.setUsername(reviewEntity.getUserEntity().getUsername());
        dto.setComment(reviewEntity.getComment());
        dto.setRating(reviewEntity.getRating());
        return dto;
    }

    public static ReviewDto toDto(ReviewEntity reviewEntity) {
        ReviewDto dto = new ReviewDto();
        dto.setUserId(reviewEntity.getUserId());
        dto.setBookId(reviewEntity.getBookId());
        dto.setComment(reviewEntity.getComment());
        dto.setRating(reviewEntity.getRating());
        return dto;
    }

    public static ReviewEntity fromRequestDto(ReviewCreateDto dto) {
        ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setUserId(dto.getUserId());
        reviewEntity.setRating(dto.getRating());
        reviewEntity.setComment(dto.getComment());
        return reviewEntity;
    }

}

