package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private boolean available;
}
