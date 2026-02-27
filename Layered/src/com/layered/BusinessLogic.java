package com.layered;

import java.util.ArrayList;

public class BusinessLogic {
	
	private FIleManager fm;
	
	public BusinessLogic(FIleManager fm) {
		this.fm=fm;
	}
	
	public String calculateCgpa(String rn) {
		ArrayList<Double> coursesGpa=fm.getData(rn);
		if(coursesGpa==null) {
			return "no data for this student";
		}
		Double totalGpa=0.0;
		for(int i=0;i<coursesGpa.size();i++) {
			totalGpa+=coursesGpa.get(i);
		}
		
		return "" + (totalGpa / coursesGpa.size());
	}
}
