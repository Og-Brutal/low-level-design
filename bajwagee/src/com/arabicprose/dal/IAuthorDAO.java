package com.arabicprose.dal;

import java.sql.SQLException;
import java.util.List;

import com.arabicprose.dto.AuthorDTO;

public interface IAuthorDAO {
    void addAuthor(AuthorDTO author) throws SQLException;
    List<AuthorDTO> getAllAuthors() throws SQLException;
    void updateAuthor(AuthorDTO author) throws SQLException;
    void deleteAuthor(int authorId) throws SQLException;
    AuthorDTO getAuthorById(int authorId) throws SQLException;
    String searchAuthor(int id) throws SQLException;
}