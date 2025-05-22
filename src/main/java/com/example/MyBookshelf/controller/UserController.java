package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.dto.UserDto;
import com.example.MyBookshelf.dto.request.UserCreateDto;
import com.example.MyBookshelf.dto.responce.UserResponseDto;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.mapper.UserMapper;
import com.example.MyBookshelf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(UserMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody UserCreateDto userRequestDto) {
        UserEntity savedUserEntity = userService.saveUser(UserMapper.fromCreateDto(userRequestDto));
        return ResponseEntity.ok(UserMapper.toDto(savedUserEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
