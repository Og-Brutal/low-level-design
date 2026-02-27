package com.lms.bll;
import com.lms.dto.Book;

public interface IBook {
    Book calculateFineForBook(String bookCode);
}
