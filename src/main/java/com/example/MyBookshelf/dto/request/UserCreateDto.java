package com.example.MyBookshelf.dto.request;

import lombok.Data;

@Data
public class UserCreateDto {
    private String email;
    private String username;
    private String password;
}

