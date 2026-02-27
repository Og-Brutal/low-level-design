package com.lms.bll;

import com.lms.dal.IBookDAO;
import com.lms.dtl.BookDTO;

public class BookBO implements IBookBO{
	private IBookDAO bookDAO ; // depend on abstraction
	
	 public BookBO ( IBookDAO bookDAO ) {
	 this . bookDAO = bookDAO ; // dependency injection
	 }
	
	 public void addBook ( BookDTO book ) {
	 bookDAO . addBook ( book ) ;
	 }
	
	 public BookDTO getBook ( int id ) {
	 return bookDAO . getBookById ( id ) ;
	 }

}
