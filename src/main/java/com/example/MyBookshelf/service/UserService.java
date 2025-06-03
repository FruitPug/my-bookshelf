package com.example.MyBookshelf.service;

import com.example.MyBookshelf.dto.responce.UserResponseDto;
import com.example.MyBookshelf.entity.ReviewEntity;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.mapper.UserMapper;
import com.example.MyBookshelf.repository.ReviewRepository;
import com.example.MyBookshelf.repository.UserBookStatusRepository;
import com.example.MyBookshelf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final UserBookStatusRepository statusRepository;
    private final ReviewService reviewService;

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }

    public ResponseEntity<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id).map(UserMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<Void> deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        for (ReviewEntity review : reviewRepository.findByUser(user)) {
            reviewService.deleteReview(review.getId());
        }

        statusRepository.deleteByUser(user);

        userRepository.delete(user);

        return ResponseEntity.noContent().build();
    }

    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

}
