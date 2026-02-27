package com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.BookDTO;

public interface IBookDAO {

	public boolean createBook(String title,int authorId,String era) ;
	public BookDTO retrieveBook(String title);
	public boolean updateBook(String oldBookName,String title,int authorId, String era);
	public boolean deleteBook(String title);
	
	
	public ArrayList<BookDTO> getAllBooks();
	public String getBookById(int id);
	
	public int searchBook(String title);

	
}
