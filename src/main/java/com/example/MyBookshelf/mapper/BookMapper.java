package com.example.MyBookshelf.mapper;

import com.example.MyBookshelf.dto.BookDto;
import com.example.MyBookshelf.dto.request.BookCreateDto;
import com.example.MyBookshelf.dto.responce.BookResponseDto;
import com.example.MyBookshelf.dto.responce.ReviewResponseDto;
import com.example.MyBookshelf.entity.Book;

import java.util.List;

public class BookMapper {

    public static BookResponseDto toResponseDto(Book book) {
        BookResponseDto dto = new BookResponseDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setGenre(book.getGenre());
        dto.setStatus(book.getStatus());
        dto.setRating(book.getRating());
        dto.setReviewCount(book.getReviewCount());

        List<ReviewResponseDto> reviewsDto = book.getReviews().stream()
                .map(ReviewMapper::toResponseDto)
                .toList();
        dto.setReviews(reviewsDto);

        return dto;
    }


    public static BookDto toDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setGenre(book.getGenre());
        dto.setStatus(book.getStatus());
        return dto;
    }

    public static Book fromCreateDto(BookCreateDto dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setGenre(dto.getGenre());
        book.setStatus(dto.getStatus());
        return book;
    }
}

