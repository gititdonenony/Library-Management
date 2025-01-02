package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.entity.Book;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private BookRequest bookRequest;
    private Book book;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        bookRequest = new BookRequest();
        bookRequest.setTitle("Test Book");
        bookRequest.setAuthor("Test Author");
        bookRequest.setIsbn("1234");
        bookRequest.setAvailable(true);

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("1234");
        book.setAvailable(true);

        bookResponse = new BookResponse();
        bookResponse.setId(1L);
        bookResponse.setTitle("Test Book");
        bookResponse.setAuthor("Test Author");
        bookResponse.setIsbn("1234");
        bookResponse.setAvailable(true);
    }
    @Test
    void testAddBook() {
        when(modelMapper.map(bookRequest, Book.class)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(modelMapper.map(book, BookResponse.class)).thenReturn(bookResponse);

        BookResponse result = bookService.addBook(bookRequest);

        assertNotNull(result);
        assertEquals(bookResponse.getId(), result.getId());
        assertEquals(bookResponse.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testGetBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(modelMapper.map(book, BookResponse.class)).thenReturn(bookResponse);

        BookResponse result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals(bookResponse.getId(), result.getId());
        assertEquals(bookResponse.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(1L));
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));
        when(modelMapper.map(book, BookResponse.class)).thenReturn(bookResponse);

        List<BookResponse> result = bookService.getAllBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testUpdateBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(modelMapper.map(book, BookResponse.class)).thenReturn(bookResponse);

        BookResponse result = bookService.updateBook(1L, bookRequest);

        assertNotNull(result);
        assertEquals(bookResponse.getId(), result.getId());
        assertEquals(bookResponse.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testDeleteBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        assertDoesNotThrow(() -> bookService.deleteBook(1L));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository, times(1)).findById(1L);
    }
}