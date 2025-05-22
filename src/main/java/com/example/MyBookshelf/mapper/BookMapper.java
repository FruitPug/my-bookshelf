package com.example.MyBookshelf.mapper;

import com.example.MyBookshelf.dto.BookDto;
import com.example.MyBookshelf.dto.request.BookCreateDto;
import com.example.MyBookshelf.dto.responce.BookResponseDto;
import com.example.MyBookshelf.dto.responce.ReviewResponseDto;
import com.example.MyBookshelf.entity.BookEntity;

import java.util.List;

public class BookMapper {

    public static BookResponseDto toResponseDto(BookEntity bookEntity) {
        BookResponseDto dto = new BookResponseDto();
        dto.setId(bookEntity.getId());
        dto.setTitle(bookEntity.getTitle());
        dto.setAuthor(bookEntity.getAuthor());
        dto.setGenre(bookEntity.getGenre());
        dto.setStatus(bookEntity.getStatus());
        dto.setRating(bookEntity.getRating());
        dto.setReviewCount(bookEntity.getReviewCount());

        List<ReviewResponseDto> reviewsDto = bookEntity.getReviewEntities().stream()
                .map(ReviewMapper::toResponseDto)
                .toList();
        dto.setReviews(reviewsDto);

        return dto;
    }


    public static BookDto toDto(BookEntity bookEntity) {
        BookDto dto = new BookDto();
        dto.setId(bookEntity.getId());
        dto.setTitle(bookEntity.getTitle());
        dto.setAuthor(bookEntity.getAuthor());
        dto.setGenre(bookEntity.getGenre());
        dto.setStatus(bookEntity.getStatus());
        return dto;
    }

    public static BookEntity fromCreateDto(BookCreateDto dto) {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle(dto.getTitle());
        bookEntity.setAuthor(dto.getAuthor());
        bookEntity.setGenre(dto.getGenre());
        bookEntity.setStatus(dto.getStatus());
        return bookEntity;
    }
}

