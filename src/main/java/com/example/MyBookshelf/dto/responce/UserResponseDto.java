package com.example.MyBookshelf.dto.responce;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private String username;
}

