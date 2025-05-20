package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.dto.ReviewDto;
import com.example.MyBookshelf.entity.Review;
import com.example.MyBookshelf.entity.User;
import com.example.MyBookshelf.repository.BookRepository;
import com.example.MyBookshelf.repository.UserRepository;
import com.example.MyBookshelf.service.ReviewService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Review> addReviewToBook(@PathVariable Long bookId, @RequestBody Review review) {
        return bookRepository.findById(bookId)
                .map(book -> {
                    review.setBookId(bookId);
                    Review savedReview = reviewService.addReview(review);
                    return ResponseEntity.ok(savedReview);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }
}
