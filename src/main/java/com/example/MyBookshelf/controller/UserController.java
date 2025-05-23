package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.dto.request.UserCreateDto;
import com.example.MyBookshelf.dto.responce.UserResponseDto;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.mapper.UserMapper;
import com.example.MyBookshelf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

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

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(Authentication auth) {
        String email = auth.getName();
        return userService.findByEmail(email)
                .map(UserMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> register(@RequestBody UserCreateDto req) {
        if (userService.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        UserEntity saved = userService.save(
                UserMapper.fromCreateDto(req, passwordEncoder)
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserMapper.toResponseDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
