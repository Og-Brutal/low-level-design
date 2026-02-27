package cdu.fast.se3043.lms.dal;

import java.util.ArrayList;

import cdu.fast.se3043.lms.dtos.*;

public class TextDataAccessLayer implements IDataAccessLayer {
	ArrayList<StudentRecord> stuData;
	public ArrayList<Book> getBookDetails(String studentId){  return new ArrayList<Book>(); 	}
	public double getTotalFine(String studentId) {	return 0.0;  }
}
