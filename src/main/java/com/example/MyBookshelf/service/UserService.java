package com.example.MyBookshelf.service;

import com.example.MyBookshelf.entity.ReviewEntity;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.repository.ReviewRepository;
import com.example.MyBookshelf.repository.UserBookStatusRepository;
import com.example.MyBookshelf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final UserBookStatusRepository statusRepository;
    private final ReviewService reviewService;

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        for (ReviewEntity r : reviewRepository.findByUser(user)) {
            reviewService.deleteReview(r.getId());
        }

        statusRepository.deleteByUser(user);

        userRepository.delete(user);
    }

    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
