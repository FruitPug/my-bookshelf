package com.example.MyBookshelf.service;

import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.entity.ReviewEntity;
import com.example.MyBookshelf.repository.BookRepository;
import com.example.MyBookshelf.repository.ReviewRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;


    public List<ReviewEntity> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional
    public ReviewEntity addReview(Long bookId, ReviewEntity reviewEntity) {
        reviewEntity.setBook(bookRepository.findById(bookId).get());
        ReviewEntity saved = reviewRepository.save(reviewEntity);

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        int oldCount = book.getReviewCount();
        double oldAvg  = book.getRating();

        int newCount = oldCount + 1;
        double newAvg = ((oldAvg * oldCount) + saved.getRating()) / newCount;

        book.setReviewCount(newCount);
        book.setRating(newAvg);

        bookRepository.save(book);

        return saved;
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));
        Long bookId = review.getBook().getId();

        reviewRepository.deleteById(reviewId);

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        List<ReviewEntity> remaining = reviewRepository.findByBookId(bookId).stream().toList();
        int count = remaining.size();
        double avg   = remaining.stream()
                .mapToDouble(ReviewEntity::getRating)
                .average()
                .orElse(0.0);

        book.setReviewCount(count);
        book.setRating(avg);

        bookRepository.save(book);
    }
}
