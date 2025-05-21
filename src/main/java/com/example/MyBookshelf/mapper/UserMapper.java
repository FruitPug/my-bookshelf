package com.example.MyBookshelf.mapper;

import com.example.MyBookshelf.dto.UserDto;
import com.example.MyBookshelf.dto.request.UserCreateDto;
import com.example.MyBookshelf.dto.responce.UserResponseDto;
import com.example.MyBookshelf.entity.User;

public class UserMapper {

    public static UserResponseDto toResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getUsername());
        return dto;
    }

    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        return dto;
    }

    public static User fromCreateDto(UserCreateDto dto) {
        User user = new User();
        user.setUsername(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }
}
