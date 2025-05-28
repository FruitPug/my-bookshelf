package com.example.MyBookshelf.mapper;

import com.example.MyBookshelf.dto.request.UserCreateDto;
import com.example.MyBookshelf.dto.responce.UserResponseDto;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserMapper {

    public static UserResponseDto toResponseDto(UserEntity userEntity) {
        return UserResponseDto.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .build();
    }

    public static UserEntity fromCreateDto(UserCreateDto dto, PasswordEncoder encoder) {
        return UserEntity.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .passwordHash(encoder.encode(dto.getPassword()))
                .role(Role.USER)
                .build();
    }
}
