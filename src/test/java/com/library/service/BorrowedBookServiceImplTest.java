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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class BorrowedBookServiceImplTest {

    @Mock
    private BorrowedBookRepository borrowedBookRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BorrowedBookServiceImpl borrowedBookService;

    private Book book;
    private User user;
    private BorrowedBookRequest borrowedBookRequest;
    private BorrowedBook borrowedBook;
    private BorrowedBookResponse borrowedBookResponse;

    @BeforeEach
    void setUp() {
        // Create a user and set the roles
        user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setPassword("password123");
        user.setEmail("johndoe@example.com");
        user.setRoles("USER"); // Add role field here

        // Create a book
        book = new Book();
        book.setId(1L);
        book.setAvailable(true);

        // Create BorrowedBookRequest
        borrowedBookRequest = new BorrowedBookRequest();
        borrowedBookRequest.setUserId(user.getId());
        borrowedBookRequest.setBookId(book.getId());
        borrowedBookRequest.setBorrowDate(LocalDate.now());

        // Create BorrowedBook
        borrowedBook = new BorrowedBook();
        borrowedBook.setId(1L);
        borrowedBook.setUser(user);
        borrowedBook.setBook(book);
        borrowedBook.setBorrowDate(LocalDate.now());
        borrowedBook.setStatus(BorrowStatus.BORROWED);

        // Create BorrowedBookResponse
        borrowedBookResponse = new BorrowedBookResponse();
        borrowedBookResponse.setId(borrowedBook.getId());
        borrowedBookResponse.setUserId(user.getId());
        borrowedBookResponse.setBookId(book.getId());
        borrowedBookResponse.setBorrowDate(borrowedBook.getBorrowDate());
        borrowedBookResponse.setStatus(String.valueOf(borrowedBook.getStatus()));
    }

    @Test
    void borrowBook_Success() {
        // Setup
        when(userRepository.findById(borrowedBookRequest.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(borrowedBookRequest.getBookId())).thenReturn(Optional.of(book));

        // Create a response with String status
        BorrowedBookResponse mappedResponse = new BorrowedBookResponse();
        mappedResponse.setId(1L);
        mappedResponse.setStatus(BorrowStatus.BORROWED.toString());  // Convert enum to String

        when(borrowedBookRepository.save(any(BorrowedBook.class))).thenReturn(borrowedBook);
        when(modelMapper.map(any(BorrowedBook.class), eq(BorrowedBookResponse.class)))
                .thenReturn(mappedResponse);

        // Execute
        BorrowedBookResponse response = borrowedBookService.borrowBook(borrowedBookRequest);

        // Verify
        assertNotNull(response);
        assertEquals(borrowedBook.getId(), response.getId());
        assertEquals(BorrowStatus.BORROWED.toString(), response.getStatus());  // Compare with String
        verify(bookRepository, times(1)).save(book);
        verify(borrowedBookRepository, times(1)).save(any(BorrowedBook.class));
    }

    @Test
    void borrowBook_UserNotFound() {
        when(userRepository.findById(borrowedBookRequest.getUserId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            borrowedBookService.borrowBook(borrowedBookRequest);
        });

        assertEquals("User not found with ID: " + borrowedBookRequest.getUserId(), exception.getMessage());
    }

    @Test
    void borrowBook_BookNotFound() {
        when(userRepository.findById(borrowedBookRequest.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(borrowedBookRequest.getBookId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            borrowedBookService.borrowBook(borrowedBookRequest);
        });

        assertEquals("Book not found with ID: " + borrowedBookRequest.getBookId(), exception.getMessage());
    }

    @Test
    void returnBook_Success() {
        BorrowedBook borrowedBookToReturn = new BorrowedBook();
        borrowedBookToReturn.setId(1L);
        borrowedBookToReturn.setUser(user);
        borrowedBookToReturn.setBook(book);
        borrowedBookToReturn.setStatus(BorrowStatus.BORROWED);

        // Mock finding the borrowed book
        when(borrowedBookRepository.findById(1L)).thenReturn(Optional.of(borrowedBookToReturn));

        // Mock saving the book
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Mock saving the borrowed book with RETURNED status
        BorrowedBook returnedBook = new BorrowedBook();
        returnedBook.setId(1L);
        returnedBook.setStatus(BorrowStatus.RETURNED);
        when(borrowedBookRepository.save(any(BorrowedBook.class))).thenReturn(returnedBook);

        // Mock the mapper to return response with RETURNED status
        BorrowedBookResponse expectedResponse = new BorrowedBookResponse();
        expectedResponse.setId(1L);
        expectedResponse.setStatus(String.valueOf(BorrowStatus.RETURNED));
        when(modelMapper.map(any(BorrowedBook.class), eq(BorrowedBookResponse.class)))
                .thenReturn(expectedResponse);

        // Execute
        BorrowedBookResponse response = borrowedBookService.returnBook(1L);

        // Verify
        assertNotNull(response);
        assertEquals(BorrowStatus.RETURNED.toString(), response.getStatus());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(borrowedBookRepository, times(1)).save(any(BorrowedBook.class));
    }

    @Test
    void returnBook_BorrowedBookNotFound() {
        when(borrowedBookRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            borrowedBookService.returnBook(1L);
        });

        assertEquals("Borrowed book not found with ID: 1", exception.getMessage());
    }

    @Test
    void returnBook_AlreadyReturned() {
        borrowedBook.setStatus(BorrowStatus.RETURNED);
        when(borrowedBookRepository.findById(1L)).thenReturn(Optional.of(borrowedBook));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            borrowedBookService.returnBook(1L);
        });

        assertEquals("Book has already been returned.", exception.getMessage());
    }

    @Test
    void getAllBorrowedBooksByUser_Success() {
        when(borrowedBookRepository.findByUserId(user.getId())).thenReturn(List.of(borrowedBook));
        when(modelMapper.map(any(BorrowedBook.class), eq(BorrowedBookResponse.class))).thenReturn(borrowedBookResponse);

        List<BorrowedBookResponse> responses = borrowedBookService.getAllBorrowedBooksByUser(user.getId());

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(borrowedBook.getId(), responses.get(0).getId());
    }

    @Test
    void getAllBorrowedBooksByUser_NoBooksFound() {
        when(borrowedBookRepository.findByUserId(user.getId())).thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            borrowedBookService.getAllBorrowedBooksByUser(user.getId());
        });

        assertEquals("No borrowed books found for user ID: " + user.getId(), exception.getMessage());
    }
}
