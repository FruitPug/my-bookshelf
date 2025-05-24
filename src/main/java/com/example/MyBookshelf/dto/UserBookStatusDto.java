package com.example.MyBookshelf.dto;

import com.example.MyBookshelf.status.ReadingStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserBookStatusDto {
    private Long bookId;
    private String bookTitle;
    private ReadingStatus status;
}
