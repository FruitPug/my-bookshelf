package com.example.MyBookshelf.mapper;

import com.example.MyBookshelf.dto.request.BookCreateDto;
import com.example.MyBookshelf.dto.responce.BookResponseDto;
import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.status.ReadingStatus;

import java.util.List;

public class BookMapper {

    public static BookResponseDto toResponseDto(BookEntity bookEntity, ReadingStatus readingStatus) {
        return BookResponseDto.builder()
                .id(bookEntity.getId())
                .title(bookEntity.getTitle())
                .author(bookEntity.getAuthor())
                .genre(bookEntity.getGenre())
                .userStatus(readingStatus)
                .rating(bookEntity.getRating())
                .reviewCount(bookEntity.getReviewCount())
                .reviews(
                        bookEntity.getReviewEntities() == null ?
                                List.of() :
                                bookEntity.getReviewEntities().stream()
                                        .map(ReviewMapper::toResponseDto)
                                        .toList()
                )
                .build();
    }

    public static BookEntity fromCreateDto(BookCreateDto dto) {
        return BookEntity.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .genre(dto.getGenre())
                .build();
    }
}

