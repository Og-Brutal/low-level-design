package com.arabicprose.bll;

import com.arabicprose.dto.AuthorDTO;
import java.sql.SQLException;
import java.util.List;

public interface IAuthorBO {
    void addAuthor(AuthorDTO authorDTO) throws SQLException;
    List<AuthorDTO> getAllAuthors() throws SQLException;
    void updateAuthor(AuthorDTO authorDTO) throws SQLException;
    void deleteAuthor(int authorId) throws SQLException;
    AuthorDTO getAuthorById(int authorId) throws SQLException;
    String searchAuthor(int id) throws SQLException;
}