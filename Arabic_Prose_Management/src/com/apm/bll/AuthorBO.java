package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.AuthorDTO;

public class AuthorBO implements IAuthorBO{
	
	private IDataAccessLayerFasade daf;
	
	
	public AuthorBO(IDataAccessLayerFasade daf)
	{
		this.daf = daf;
	}

	@Override
	public boolean createAuthor(String name, String biography){
		if(daf.searchAuthor(name)!=-1) {
			return false;
		}
		return daf.createAuthor(name, biography);
	}

	@Override
	public AuthorDTO retrieveAuthor(String name) {
		return daf.retrieveAuthor(name);
	}

	@Override
	public boolean updateAuthor(String oldAuthorName,String name, String biography) {
		System.out.println(name);
		return daf.updateAuthor(oldAuthorName, name, biography);
	}

	@Override
	public boolean deleteAuthor(String name) {
		return daf.deleteAuthor(name);
	}

	@Override
	public int searchAuthor(String name) {
		return daf.searchAuthor(name);
	}

	@Override
	public ArrayList<AuthorDTO> getAllAuthors() {
		return daf.getAllAuthors();
	}

	@Override
	public String getAuthorById(int id) {
		return daf.getAuthorById(id);
	}

}
