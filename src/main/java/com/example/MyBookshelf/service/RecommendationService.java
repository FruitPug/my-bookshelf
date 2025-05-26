package com.example.MyBookshelf.service;

import com.example.MyBookshelf.entity.*;
import com.example.MyBookshelf.repository.UserBookStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserBookStatusRepository statusRepo;
    private final BookService bookService;

    @Cacheable(value = "recommendations", key = "#user.id")
    public Page<BookEntity> recommendForUser(UserEntity user, Pageable pageable) {
        // 1) Exclude books already seen
        Set<BookEntity> excluded = statusRepo.findByUser(user).stream()
                .map(UserBookStatusEntity::getBook)
                .collect(Collectors.toSet());

        // 2) Determine top genre
        String topGenre = statusRepo.findByUser(user).stream()
                .map(ubs -> ubs.getBook().getGenre())
                .collect(Collectors.groupingBy(g -> g, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // 3) Build candidates
        List<BookEntity> candidates;
        if (topGenre == null) {
            candidates = bookService.findTopReviewed(50);
        } else {
            candidates = bookService.getBooksByGenre(topGenre).stream()
                    .sorted(Comparator.comparingInt(BookEntity::getReviewCount).reversed())
                    .limit(50)
                    .collect(Collectors.toList());
        }

        // 4) Filter out excluded and collect
        List<BookEntity> filtered = candidates.stream()
                .filter(b -> !excluded.contains(b))
                .collect(Collectors.toList());

        // 5) Now apply paging
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<BookEntity> pageContent = start > end ? Collections.emptyList() : filtered.subList(start, end);

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }
}
