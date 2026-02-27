package com.lms.dto;


import java.util.ArrayList;

public class StudentDTO {
	int studentID;
	String studentName;
	ArrayList<BookDTO> borrowedBook;
	
	
	public StudentDTO() {}
	
	
	public StudentDTO(int sid,String sname,ArrayList<BookDTO> bk) {
		studentID=sid;
		studentName=sname;
		borrowedBook=bk;
	}
	
	public void setID(int id) {
		this.studentID=id;
	}
	
	public void setName(String name) {
		this.studentName=name;
	}
	
	public void setList(ArrayList<BookDTO> bk) {
		this.borrowedBook=bk;
	}
	
	public int getID() {
		return this.studentID;
	}
	
	public String getName() {
		return this.studentName;
	}
	
	public ArrayList<BookDTO> getBorrowBooks(){
		return this.borrowedBook;
	}

	
}
