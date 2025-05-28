package com.example.MyBookshelf.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateDto {
    @NotBlank(message="Email is required")
    private String email;

    @NotBlank(message="Username is required")
    private String username;

    @NotBlank(message="Password is required")
    private String password;
}

