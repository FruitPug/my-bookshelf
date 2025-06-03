package com.example.MyBookshelf.service;

import com.example.MyBookshelf.dto.responce.BookResponseDto;
import com.example.MyBookshelf.entity.*;
import com.example.MyBookshelf.repository.UserBookStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserBookStatusRepository statusRepository;
    private final BookService bookService;

    @Cacheable(value = "recommendations", key = "#user.id")
    public ResponseEntity<Page<BookResponseDto>> recommendForUser(
            UserEntity user,
            Pageable pageable
    ) {
        int size = pageable.getPageSize();
        Page<UserBookStatusEntity> statuses = statusRepository
                .findByUser(user, pageable);

        Set<Long> excludedIds = statuses.stream()
                .map(ubs -> ubs.getBook().getId())
                .collect(Collectors.toSet());

        String topGenre = statuses.stream()
                .map(ubs -> ubs.getBook().getGenre())
                .collect(Collectors.groupingBy(g -> g, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Page<BookResponseDto> candidates;
        if (topGenre == null) {
            candidates = bookService.findTopRatedDto(size);
        } else {
            PageRequest candidatePage = PageRequest.of(0, size);
            candidates = bookService.getBooksByGenreDto(topGenre, candidatePage);
        }

        List<BookResponseDto> allCandidates = candidates.getContent();

        List<BookResponseDto> filtered = allCandidates.stream()
                .filter(dto -> !excludedIds.contains(dto.getId()))
                .toList();

        return ResponseEntity.ok(new PageImpl<>(filtered, pageable, filtered.size()));
    }

}
