package com.dani;

public class StudentCgpaApp {
	
	public static void main(String[] arg) {
		StudentDataReader studentDataReader = new StudentDataReader();
		CgpaCalculator cgpaCalculator = new CgpaCalculator(studentDataReader);
		StudentCgpaWindow window = new StudentCgpaWindow(cgpaCalculator);
		window.setVisible(true);
	}
}
