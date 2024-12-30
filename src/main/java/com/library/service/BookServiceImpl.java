package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.entity.Book;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService{
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BookResponse addBook(BookRequest bookRequest) {
        try {
            Book book = modelMapper.map(bookRequest, Book.class);
            book.setAvailable(true); // Default to available when a book is added
            Book savedBook = bookRepository.save(book);
            return modelMapper.map(savedBook, BookResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while adding the book: " + e.getMessage(), e);
        }
    }

    @Override
    public BookResponse getBookById(Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
            return modelMapper.map(book, BookResponse.class);
        } catch (ResourceNotFoundException e) {
            throw e; // Rethrow the specific exception
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching the book: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BookResponse> getAllBooks() {
        try {
            List<Book> books = bookRepository.findAll();
            return books.stream()
                    .map(book -> modelMapper.map(book, BookResponse.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching all books: " + e.getMessage(), e);
        }
    }

    @Override
    public BookResponse updateBook(Long id, BookRequest bookRequest) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
            modelMapper.map(bookRequest, book); // Update book fields from request
            Book updatedBook = bookRepository.save(book);
            return modelMapper.map(updatedBook, BookResponse.class);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while updating the book: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteBook(Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
            bookRepository.delete(book);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while deleting the book: " + e.getMessage(), e);
        }
    }
}
