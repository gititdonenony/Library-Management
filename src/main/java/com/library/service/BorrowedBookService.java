package com.library.service;

import com.library.dto.BorrowedBookRequest;
import com.library.dto.BorrowedBookResponse;

import java.util.List;

public interface BorrowedBookService {
    BorrowedBookResponse borrowBook(BorrowedBookRequest bookRequest);
    BorrowedBookResponse returnBook(long bookId);
    List<BorrowedBookResponse> getAllBorrowedBooksByUser(Long id);
}
