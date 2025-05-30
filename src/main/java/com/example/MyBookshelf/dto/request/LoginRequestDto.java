package com.example.MyBookshelf.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message="Email is required")
    private String email;

    @NotBlank(message="Password is required")
    private String password;
}
