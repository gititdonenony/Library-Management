package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;

import java.util.List;

public interface BookService {
    BookResponse addBook(BookRequest bookRequest);
    BookResponse getBookById(Long id);
    List<BookResponse> getAllBooks();
    BookResponse updateBook(Long id, BookRequest bookRequest);
    void deleteBook(Long id);

}
