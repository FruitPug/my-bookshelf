package com.example.MyBookshelf.mapper;

import com.example.MyBookshelf.dto.ReviewDto;
import com.example.MyBookshelf.dto.request.ReviewCreateDto;
import com.example.MyBookshelf.dto.responce.ReviewResponseDto;
import com.example.MyBookshelf.entity.Review;

public class ReviewMapper {

    public static ReviewResponseDto toResponseDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setBookId(review.getBookId());
        dto.setUsername(review.getUser().getUsername());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        return dto;
    }

    public static ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setUserId(review.getUserId());
        dto.setBookId(review.getBookId());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        return dto;
    }

    public static Review fromRequestDto(ReviewCreateDto dto) {
        Review review = new Review();
        review.setUserId(dto.getUserId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return review;
    }

}

