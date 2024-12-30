package com.library.repository;
import com.library.entity.BorrowedBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {
    List<BorrowedBook> findByUserId(Long userId);
    Optional<BorrowedBook> findByUserIdAndBookId(Long userId, Long bookId);
}