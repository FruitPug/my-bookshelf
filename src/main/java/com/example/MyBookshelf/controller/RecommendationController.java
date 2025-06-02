package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.dto.responce.BookResponseDto;
import com.example.MyBookshelf.entity.UserEntity;
import com.example.MyBookshelf.service.CurrentUserService;
import com.example.MyBookshelf.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<Page<BookResponseDto>> recommend(
            Pageable pageable
    ) {
        UserEntity user = currentUserService.get();

        Page<BookResponseDto> dtoPage = recService.recommendForUser(user, pageable);
        return ResponseEntity.ok(dtoPage);
    }
}
