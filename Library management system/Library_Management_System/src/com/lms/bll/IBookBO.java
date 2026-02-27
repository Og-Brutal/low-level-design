package com.lms.bll;

import com.lms.dtl.BookDTO;

public interface IBookBO {
	
	

	 public void addBook ( BookDTO book ); 
	
	 public BookDTO getBook ( int id );  
	
}
