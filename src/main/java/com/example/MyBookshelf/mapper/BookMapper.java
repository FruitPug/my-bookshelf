package com.example.MyBookshelf.mapper;

import com.example.MyBookshelf.dto.BookDto;
import com.example.MyBookshelf.dto.request.BookCreateDto;
import com.example.MyBookshelf.dto.responce.BookResponseDto;
import com.example.MyBookshelf.entity.BookEntity;

public class BookMapper {

    public static BookResponseDto toResponseDto(BookEntity bookEntity) {
        return BookResponseDto.builder()
                .id(bookEntity.getId())
                .title(bookEntity.getTitle())
                .author(bookEntity.getAuthor())
                .genre(bookEntity.getGenre())
                .status(bookEntity.getStatus())
                .rating(bookEntity.getRating())
                .reviewCount(bookEntity.getReviewCount())
                .reviews(bookEntity.getReviewEntities().stream()
                        .map(ReviewMapper::toResponseDto)
                        .toList())
                .build();
    }


    public static BookDto toDto(BookEntity bookEntity) {
        return BookDto.builder()
                .id(bookEntity.getId())
                .title(bookEntity.getTitle())
                .author(bookEntity.getAuthor())
                .genre(bookEntity.getGenre())
                .status(bookEntity.getStatus())
                .build();
    }

    public static BookEntity fromCreateDto(BookCreateDto dto) {
        return BookEntity.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .genre(dto.getGenre())
                .status(dto.getStatus())
                .build();
    }
}

