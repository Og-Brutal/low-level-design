package com.abft.dal;

public interface IBookDAO {
    void addBook(Book book);
    Book getBookById(int id);
}
