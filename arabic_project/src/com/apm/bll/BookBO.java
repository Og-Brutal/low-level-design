package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.BookDTO;

public class BookBO implements IBookBO {
	
	private IDataAccessLayerFasade daf;
	
	
	public BookBO(IDataAccessLayerFasade daf)
	{
		this.daf = daf;
	}

	@Override
	public boolean createBook(String title, String authorName, String era) {
		
		int authorId=daf.searchAuthor(authorName);
		 if(authorId==-1) {
			 return false;
		 }
		 
		 if(daf.searchBook(title)!=-1) {
			 return false;
		 }
		 return daf.createBook(title, authorId, era);
	}

	@Override
	public BookDTO retrieveBook(String title) {
		
		return daf.retrieveBook(title);
	}

	@Override
	public boolean updateBook(String oldBookName,String title,String authorName, String era){
		System.out.println(authorName);
		int authorId=daf.searchAuthor(authorName);
		 if(authorId==-1) {
			 return false;
		 }
		 return daf.updateBook(oldBookName,title,authorId,era);
	}
		
	@Override
	public boolean deleteBook(String title) {
		return daf.deleteBook(title);
	}

	@Override
	public int searchBook(String title) {
		return daf.searchBook(title);
	}

	@Override
	public ArrayList<BookDTO> getAllBooks() {
		return daf.getAllBooks();
	}

	@Override
	public String getBookById(int id) {
		return daf.getBookById(id);
	}

	
	

}
