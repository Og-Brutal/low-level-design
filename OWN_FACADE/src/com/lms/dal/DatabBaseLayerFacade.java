package com.lms.dal;

import java.awt.print.Book;

public class DatabBaseLayerFacade implements IDataAccesLayerFacade {
	IBookDAO bkd;
	IStudentDAO isd;
		
	public DatabBaseLayerFacade() {};
	
	public DatabBaseLayerFacade(IBookDAO bkd,IStudentDAO isd) {
		this.bkd=bkd;
		this.isd=isd;
	}
	
	@Override
	public double getTotalFine(int studentId) {
		return isd.getTotalFine(studentId);
	}

	@Override
	public Book getBook(String isbn) {
		return bkd.getBook(isbn);
	}
	
}
	