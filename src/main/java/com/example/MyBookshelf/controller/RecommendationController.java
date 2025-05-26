package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.dto.responce.BookResponseDto;
import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.mapper.BookMapper;
import com.example.MyBookshelf.repository.UserRepository;
import com.example.MyBookshelf.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recService;
    private final UserRepository userRepo;

    @GetMapping
    public ResponseEntity<Page<BookResponseDto>> recommend(
            Authentication auth,
            Pageable pageable
    ) {
        UserEntity user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Page<BookEntity> page = recService.recommendForUser(user, pageable);
        Page<BookResponseDto> dtoPage = page.map(book ->
                BookMapper.toResponseDto(book, /* userStatus= */ null)
        );
        return ResponseEntity.ok(dtoPage);
    }
}
