package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.dto.BookStatusDto;
import com.example.MyBookshelf.dto.UserBookStatusDto;
import com.example.MyBookshelf.dto.request.BookCreateDto;
import com.example.MyBookshelf.dto.responce.BookResponseDto;
import com.example.MyBookshelf.entity.BookEntity;
import com.example.MyBookshelf.service.UserBookStatusService;
import com.example.MyBookshelf.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final UserBookStatusService statusService;

    @GetMapping
    public Page<BookResponseDto> getAllBooks(
            Pageable pageable
    ) {
        return bookService.getAllBooksDto(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(
            @PathVariable Long id
    ) {
        return bookService.getBookByIdDto(id);
    }

    @GetMapping("/top-reviewed")
    public Page<BookResponseDto> topReviewed(
            @RequestParam(defaultValue = "5") int n
    ) {
        return bookService.findTopReviewedDto(n);
    }

    @GetMapping("/least-reviewed")
    public Page<BookResponseDto> leastReviewed(
            @RequestParam(defaultValue = "5") int n
    ) {
        return bookService.findLeastReviewedDto(n);
    }

    @GetMapping("/top-rated")
    public Page<BookResponseDto> topRated(
            @RequestParam(defaultValue = "5") int n
    ) {
        return bookService.findTopRatedDto(n);
    }

    @GetMapping("/least-rated")
    public Page<BookResponseDto> leastRated(
            @RequestParam(defaultValue = "5") int n
    ) {
        return bookService.findLeastRatedDto(n);
    }

    @GetMapping("/filter/genre/{genre}")
    public Page<BookResponseDto> getByGenre(
            @PathVariable String genre,
            Pageable pageable
    ) {
        return bookService.getBooksByGenreDto(genre, pageable);
    }

    @GetMapping("/filter/genre-async/{genre}")
    public CompletableFuture<ResponseEntity<List<BookResponseDto>>> getBooksByGenreAsyncWithFilter(
            @PathVariable String genre
    ) {
        return bookService.getBooksByGenreWithAsyncRatingFilter(genre)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/filter/status/{status}")
    public Page<BookResponseDto> getByStatus(
            @PathVariable String status,
            Pageable pageable
    ) {
        return bookService.getBooksByStatusDto(status, pageable);
    }

    @GetMapping("/status")
    public ResponseEntity<Page<UserBookStatusDto>> listStatuses(Pageable pageable) {
        return bookService.listStatuses(pageable);
    }

    @PostMapping
    public ResponseEntity<BookResponseDto> addBook(@Valid @RequestBody BookCreateDto dto) {
        return bookService.addBook(dto);
    }

    @PostMapping("/status/{bookId}")
    public ResponseEntity<UserBookStatusDto> setStatus(
            @PathVariable Long bookId,
            @Valid @RequestBody BookStatusDto statusDto
    ) {
        BookEntity book = bookService.findById(bookId);
        return statusService.setStatus(book, statusDto.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        return bookService.deleteBook(id);
    }
}
