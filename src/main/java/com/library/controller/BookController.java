package com.library.controller;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody BookRequest bookRequest) {
        try {
            BookResponse bookResponse = bookService.addBook(bookRequest);
            return new ResponseEntity<>(bookResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add book: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        try {
            BookResponse bookResponse = bookService.getBookById(id);
            return new ResponseEntity<>(bookResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to fetch book: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<?> getAllBooks() {
        try {
            List<BookResponse> books = bookService.getAllBooks();
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to fetch books: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookRequest bookRequest) {
        try {
            BookResponse bookResponse = bookService.updateBook(id, bookRequest);
            return new ResponseEntity<>(bookResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update book: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return new ResponseEntity<>("Book deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete book: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
