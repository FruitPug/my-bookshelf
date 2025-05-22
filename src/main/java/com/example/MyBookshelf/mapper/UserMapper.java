package com.example.MyBookshelf.mapper;

import com.example.MyBookshelf.dto.UserDto;
import com.example.MyBookshelf.dto.request.UserCreateDto;
import com.example.MyBookshelf.dto.responce.UserResponseDto;
import com.example.MyBookshelf.entity.UserEntity;

public class UserMapper {

    public static UserResponseDto toResponseDto(UserEntity userEntity) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(userEntity.getId());
        dto.setName(userEntity.getUsername());
        return dto;
    }

    public static UserDto toDto(UserEntity userEntity) {
        UserDto dto = new UserDto();
        dto.setId(userEntity.getId());
        dto.setUsername(userEntity.getUsername());
        return dto;
    }

    public static UserEntity fromCreateDto(UserCreateDto dto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(dto.getName());
        userEntity.setEmail(dto.getEmail());
        return userEntity;
    }
}
