package com.example.MyBookshelf.service;

import com.example.MyBookshelf.dto.request.ReviewCreateDto;
import com.example.MyBookshelf.dto.responce.ReviewResponseDto;
import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.entity.ReviewEntity;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.mapper.ReviewMapper;
import com.example.MyBookshelf.repository.BookRepository;
import com.example.MyBookshelf.repository.ReviewRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final CurrentUserService currentUserService;
    private final BookService bookService;


    public List<ReviewResponseDto> getReviewsByUserEmail() {
        UserEntity user = currentUserService.get();
        return reviewRepository.findByUser(user).stream()
                .map(ReviewMapper::toResponseDto)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "recommendations", key = "#user.id")
    public ResponseEntity<ReviewResponseDto> addReview(Long bookId, ReviewCreateDto dto, UserEntity user) {
        BookEntity book = bookService.findById(bookId);

        ReviewEntity reviewEntity = ReviewMapper.fromRequestDto(dto);
        reviewEntity.setBook(book);
        reviewEntity.setUser(user);

        reviewRepository.findByUserAndBook(user, book)
                .ifPresent(r -> {
                    throw new IllegalStateException("You have already reviewed this book");
                });

        int oldCount = book.getReviewCount();
        double oldAvg  = book.getRating();

        int newCount = oldCount + 1;
        double newAvg = ((oldAvg * oldCount) + reviewEntity.getRating()) / newCount;

        book.setReviewCount(newCount);
        book.setRating(newAvg);

        bookRepository.save(book);

        return ResponseEntity.ok(ReviewMapper.toResponseDto(reviewEntity));
    }

    @Transactional
    public ResponseEntity<Void> deleteReview(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));
        Long bookId = review.getBook().getId();

        reviewRepository.deleteById(reviewId);

        List<ReviewEntity> remaining = reviewRepository.findByBookId(bookId).stream().toList();
        int count = remaining.size();
        double avg   = remaining.stream()
                .mapToDouble(ReviewEntity::getRating)
                .average()
                .orElse(0.0);

        BookEntity book = review.getBook();
        book.setReviewCount(count);
        book.setRating(avg);

        bookRepository.save(book);

        return ResponseEntity.noContent().build();
    }
}
