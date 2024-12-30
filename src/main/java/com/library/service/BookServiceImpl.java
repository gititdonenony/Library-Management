package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.repository.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return null;
    }

    @Override
    public BookResponse getBookById(Long id) {
        return null;
    }

    @Override
    public List<BookResponse> getAllBooks() {
        return List.of();
    }

    @Override
    public BookResponse updateBook(Long id, BookRequest bookRequest) {
        return null;
    }

    @Override
    public void deleteBook(Long id) {

    }
}
