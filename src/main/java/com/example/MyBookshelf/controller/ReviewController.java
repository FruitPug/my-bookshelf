package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.dto.request.ReviewCreateDto;
import com.example.MyBookshelf.dto.responce.ReviewResponseDto;
import com.example.MyBookshelf.entity.ReviewEntity;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.mapper.ReviewMapper;
import com.example.MyBookshelf.repository.BookRepository;
import com.example.MyBookshelf.repository.UserRepository;
import com.example.MyBookshelf.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @GetMapping
    public List<ReviewResponseDto> getUserReviews(Authentication authentication) {
        String userEmail = authentication.getName();
        return reviewService.getReviewsByUserEmail(userEmail).stream()
                .map(ReviewMapper::toResponseDto)
                .toList();
    }

    @PostMapping("/book/{bookId}")
    public ResponseEntity<ReviewResponseDto> addReview(
            @PathVariable Long bookId,
            @RequestBody ReviewCreateDto dto,
            Authentication authentication
    ) {
        try {
            var book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

            String email = authentication.getName();
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

            ReviewEntity reviewEntity = ReviewMapper.fromRequestDto(dto);
            reviewEntity.setBook(book);
            reviewEntity.setUser(user);

            ReviewEntity saved = reviewService.addReview(bookId, reviewEntity, user);
            ReviewResponseDto response = ReviewMapper.toResponseDto(saved);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
