package com.example.MyBookshelf.controller;

import com.example.MyBookshelf.dto.BookDto;
import com.example.MyBookshelf.dto.request.BookCreateDto;
import com.example.MyBookshelf.dto.responce.BookResponseDto;
import com.example.MyBookshelf.entity.Book;
import com.example.MyBookshelf.mapper.BookMapper;
import com.example.MyBookshelf.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public Page<BookResponseDto> getAllBooks(Pageable pageable) {
        return bookService.getAllBooks(pageable)
                .map(BookMapper::toResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(BookMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filter/genre/{genre}")
    public List<BookResponseDto> getByGenre(@PathVariable String genre) {
        return bookService.getBooksByGenre(genre).stream()
                .map(BookMapper::toResponseDto)
                .toList();
    }

    @GetMapping("/filter/status/{status}")
    public List<BookResponseDto> getByStatus(@PathVariable String status) {
        return bookService.getBooksByStatus(status).stream()
                .map(BookMapper::toResponseDto)
                .toList();
    }


    @PostMapping
    public ResponseEntity<BookDto> addBook(@RequestBody BookCreateDto bookRequestDto) {
        Book savedBook = bookService.saveBook(BookMapper.fromCreateDto(bookRequestDto));
        return ResponseEntity.ok(BookMapper.toDto(savedBook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}


