package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.dto.request.ReviewCreateDto;
import com.example.MyBookshelf.dto.responce.ReviewResponseDto;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.service.CurrentUserService;
import com.example.MyBookshelf.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<ReviewResponseDto> getUserReviews() {
        return reviewService.getReviewsByUserEmail();
    }

    @PostMapping("/book/{bookId}")
    public ResponseEntity<ReviewResponseDto> addReview(
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewCreateDto dto
    ) {
        UserEntity user = currentUserService.get();
        return reviewService.addReview(bookId, dto, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        return reviewService.deleteReview(id);
    }
}
