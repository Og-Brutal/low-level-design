package com.lms.bll;

import java.awt.print.Book;

import com.lms.dal.IDataAccesLayerFacade;

public class BookBO  implements IBookBO{
	private IDataAccesLayerFacade dal;
	
	public BookBO() {}
	
	public BookBO(IDataAccesLayerFacade dal) {
		this.dal=dal;
	}
	@Override
	public Book getBook(String isbn) {
		return dal.getBook(isbn);
	}

}
