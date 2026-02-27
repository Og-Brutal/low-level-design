package com.arabicprose.bll;

import java.sql.SQLException;
import java.util.List;

import com.arabicprose.dal.IDataAccessLayerFacade;
import com.arabicprose.dto.AuthorDTO;


public class AuthorBO implements IAuthorBO {
    private IDataAccessLayerFacade authorDAO;
    
    public AuthorBO(IDataAccessLayerFacade authorDAO)
    {
    	this.authorDAO = authorDAO;
    }

    @Override
    public void addAuthor(AuthorDTO authorDTO) throws SQLException {
        if (authorDTO.getName() == null || authorDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Author name is required.");
        }
        authorDAO.addAuthor(authorDTO);
        authorDTO.setAuthorId(authorDTO.getAuthorId()); // Update DTO with generated ID
    }

    @Override
    public List<AuthorDTO> getAllAuthors() throws SQLException {
        return authorDAO.getAllAuthors();
    }

    @Override
    public void updateAuthor(AuthorDTO authorDTO) throws SQLException {
        if (authorDTO.getName() == null || authorDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Author name is required.");
        }
        authorDAO.updateAuthor(authorDTO);
    }

    @Override
    public void deleteAuthor(int authorId) throws SQLException {
        authorDAO.deleteAuthor(authorId);
    }

    @Override
    public AuthorDTO getAuthorById(int authorId) throws SQLException {
        AuthorDTO author = authorDAO.getAuthorById(authorId);
        return author;
    }

	@Override
	public String searchAuthor(int id) throws SQLException {
		return authorDAO.searchAuthor(id);
	}
}