package com.library.controller;

import com.library.dto.BorrowedBookRequest;
import com.library.dto.BorrowedBookResponse;
import com.library.exception.ResourceNotFoundException;
import com.library.service.BorrowedBookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    @RestController
    @RequestMapping("/api/borrowed-books")
    public class BorrowedBookController {
        private static final Logger logger = LoggerFactory.getLogger(BorrowedBookController.class);
        private final BorrowedBookService borrowedBookService;

        @Autowired
        public BorrowedBookController(BorrowedBookService borrowedBookService) {
            this.borrowedBookService = borrowedBookService;
        }

        // Endpoint to borrow a book
        @PostMapping
        public ResponseEntity<BorrowedBookResponse> borrowBook(@RequestBody BorrowedBookRequest borrowedBookRequest) {
            try {
                logger.info("Borrowing book with request: {}", borrowedBookRequest);
                BorrowedBookResponse borrowedBookResponse = borrowedBookService.borrowBook(borrowedBookRequest);
                return new ResponseEntity<>(borrowedBookResponse, HttpStatus.CREATED);
            } catch (ResourceNotFoundException e) {
                logger.error("Error borrowing book: {}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                logger.error("Unexpected error: {}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        @PostMapping("{bookId}")
        public ResponseEntity<BorrowedBookResponse> returnBook(@PathVariable long bookId) {
            try {
                logger.info("Returning book with ID: {}", bookId);
                BorrowedBookResponse borrowedBookResponse = borrowedBookService.returnBook(bookId);
                return new ResponseEntity<>(borrowedBookResponse, HttpStatus.OK);
            } catch (ResourceNotFoundException e) {
                logger.error("Error returning book: {}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                logger.error("Unexpected error: {}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        @GetMapping("/{userId}")
        public ResponseEntity<List<BorrowedBookResponse>> getAllBorrowedBooksByUser(@PathVariable Long userId) {
            try {
                logger.info("Fetching all borrowed books for user ID: {}", userId);
                List<BorrowedBookResponse> borrowedBooks = borrowedBookService.getAllBorrowedBooksByUser(userId);
                return new ResponseEntity<>(borrowedBooks, HttpStatus.OK);
            } catch (ResourceNotFoundException e) {
                logger.error("No borrowed books found for user ID: {}", userId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                logger.error("Unexpected error: {}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }
