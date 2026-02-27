package com.lms.dal;

import com.lms.dtl.BookDTO;

public interface IBookDAO {
 void addBook ( BookDTO book ) ;
 BookDTO getBookById ( int id ) ;
 }
