package com.apm.bll;

import java.util.ArrayList;

import com.apm.dto.BookDTO;

public interface IBookBO {
	
	public boolean createBook(String title,String authorName,String era);
	public BookDTO retrieveBook(String title);
	public boolean updateBook(String oldBookName,String title,String authorName, String era);
	public boolean deleteBook(String title);
	
	public ArrayList<BookDTO> getAllBooks();
	public String getBookById(int id);
	
	public int searchBook(String title);

}
