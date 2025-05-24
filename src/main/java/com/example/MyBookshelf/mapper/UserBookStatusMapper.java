package com.example.MyBookshelf.mapper;

import com.example.MyBookshelf.dto.UserBookStatusDto;
import com.example.MyBookshelf.entity.UserBookStatusEntity;

public class UserBookStatusMapper {
    public static UserBookStatusDto toDto(UserBookStatusEntity ubs) {
        return UserBookStatusDto.builder()
                .bookId(ubs.getBook().getId())
                .bookTitle(ubs.getBook().getTitle())
                .status(ubs.getStatus())
                .build();
    }
}
