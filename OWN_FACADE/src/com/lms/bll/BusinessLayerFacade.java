package com.lms.bll;

import java.awt.print.Book;

public class BusinessLayerFacade implements IBusinessFacade {
	IBookBO bb;
	IStudentBO sb;
	
	public BusinessLayerFacade() {}

	public BusinessLayerFacade(IBookBO bb,IStudentBO sb) {
		this.bb=bb;
		this.sb=sb;
	}
	
	@Override
	public Book getBook(String isbn) {
		return bb.getBook(isbn);
	}

	@Override
	public double getTotalFine(int studentId) {
		return sb.getTotalFine(studentId);
	}

}




