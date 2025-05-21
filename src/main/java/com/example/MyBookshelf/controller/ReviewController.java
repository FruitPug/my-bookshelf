package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.dto.ReviewDto;
import com.example.MyBookshelf.dto.request.ReviewCreateDto;
import com.example.MyBookshelf.dto.responce.ReviewResponseDto;
import com.example.MyBookshelf.entity.Review;
import com.example.MyBookshelf.entity.User;
import com.example.MyBookshelf.mapper.ReviewMapper;
import com.example.MyBookshelf.repository.BookRepository;
import com.example.MyBookshelf.repository.UserRepository;
import com.example.MyBookshelf.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @PostMapping("/book/{bookId}")
    public ResponseEntity<ReviewDto> addReview(@PathVariable Long bookId, @RequestBody ReviewCreateDto reviewCreateDto) {
        return bookRepository.findById(bookId).map(book -> {
            Optional<User> optionalUser = userRepository.findById(reviewCreateDto.getUserId());
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).<ReviewDto>body(null);
            }

            Review review = reviewService.addReview(bookId, ReviewMapper.fromRequestDto(reviewCreateDto));

            return ResponseEntity.ok(ReviewMapper.toDto(review));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ReviewResponseDto> getAllReviews() {
        return reviewService.getAllReviews().stream()
                .map(ReviewMapper::toResponseDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
