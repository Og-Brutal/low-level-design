package com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.AuthorDTO;

public interface IAuthorDAO {
	
	public boolean createAuthor(String name, String biography);
	public AuthorDTO retrieveAuthor(String name);
	public boolean updateAuthor(String oldAuthorName,String name, String biography) ;
	public boolean deleteAuthor(String name);
	
	
	public ArrayList<AuthorDTO> getAllAuthors();
	public String getAuthorById(int id);
	
	public int searchAuthor(String name);
	

}
