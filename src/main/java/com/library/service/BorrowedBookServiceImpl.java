package com.library.service;

import com.library.dto.BorrowedBookRequest;
import com.library.dto.BorrowedBookResponse;
import com.library.entity.Book;
import com.library.entity.BorrowStatus;
import com.library.entity.BorrowedBook;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowedBookRepository;
import com.library.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowedBookServiceImpl implements BorrowedBookService{
    private final BorrowedBookRepository borrowedBookRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BorrowedBookServiceImpl(BorrowedBookRepository borrowedBookRepository,
                                   BookRepository bookRepository,
                                   UserRepository userRepository,
                                   ModelMapper modelMapper) {
        this.borrowedBookRepository = borrowedBookRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BorrowedBookResponse borrowBook(BorrowedBookRequest bookRequest) {
        try {
            // Fetch user and book
            User user = userRepository.findById(bookRequest.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + bookRequest.getUserId()));

            Book book = bookRepository.findById(bookRequest.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookRequest.getBookId()));

            if (!book.isAvailable()) {
                throw new IllegalStateException("Book is not available for borrowing.");
            }

            // Create BorrowedBook
            BorrowedBook borrowedBook = new BorrowedBook();
            borrowedBook.setUser(user);
            borrowedBook.setBook(book);
            borrowedBook.setBorrowDate(bookRequest.getBorrowDate() != null ? bookRequest.getBorrowDate() : LocalDate.now());
            borrowedBook.setStatus(BorrowStatus.BORROWED);

            // Save borrowed book and update book availability
            book.setAvailable(false);
            bookRepository.save(book);

            BorrowedBook savedBorrowedBook = borrowedBookRepository.save(borrowedBook);
            return modelMapper.map(savedBorrowedBook, BorrowedBookResponse.class);

        } catch (ResourceNotFoundException | IllegalStateException e) {
            throw e; // Rethrow specific exceptions
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while borrowing the book: " + e.getMessage(), e);
        }
    }

    @Override
    public BorrowedBookResponse returnBook(long bookId) {
        try {
            // Fetch borrowed book
            BorrowedBook borrowedBook = borrowedBookRepository.findById(bookId)
                    .orElseThrow(() -> new ResourceNotFoundException("Borrowed book not found with ID: " + bookId));

            if (borrowedBook.getStatus() == BorrowStatus.RETURNED) {
                throw new IllegalStateException("Book has already been returned.");
            }

            // Update status and return date
            borrowedBook.setStatus(BorrowStatus.RETURNED);
            borrowedBook.setReturnDate(LocalDate.now());

            // Update book availability
            Book book = borrowedBook.getBook();
            book.setAvailable(true);
            bookRepository.save(book);

            BorrowedBook updatedBorrowedBook = borrowedBookRepository.save(borrowedBook);
            return modelMapper.map(updatedBorrowedBook, BorrowedBookResponse.class);

        } catch (ResourceNotFoundException | IllegalStateException e) {
            throw e; // Rethrow specific exceptions
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while returning the book: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BorrowedBookResponse> getAllBorrowedBooksByUser(Long userId) {
        try {
            List<BorrowedBook> borrowedBooks = borrowedBookRepository.findByUserId(userId);
            if (borrowedBooks.isEmpty()) {
                throw new ResourceNotFoundException("No borrowed books found for user ID: " + userId);
            }
            return borrowedBooks.stream()
                    .map(borrowedBook -> modelMapper.map(borrowedBook, BorrowedBookResponse.class))
                    .collect(Collectors.toList());

        } catch (ResourceNotFoundException e) {
            throw e; // Rethrow specific exception
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching borrowed books: " + e.getMessage(), e);
        }
    }
}
