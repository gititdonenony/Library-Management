package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowedBookRequest {

    private Long userId;
    private Long bookId;
    private LocalDate borrowDate;

}
