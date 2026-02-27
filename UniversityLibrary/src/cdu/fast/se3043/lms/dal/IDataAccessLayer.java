package cdu.fast.se3043.lms.dal;

import java.util.ArrayList;

import cdu.fast.se3043.lms.dtos.Book;

public interface IDataAccessLayer {
	public ArrayList<Book> getBookDetails(String studentId);
	public double getTotalFine(String studentId);
	
}
