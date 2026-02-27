package com.dal;

import java.util.List;

public class DALFacade implements IDALFacade {

    // ✅ Dependency Injection: interface type variable
    private IStudent studentDAL;

    // ✅ Constructor Injection
    public DALFacade(IStudent studentDAL) {
        this.studentDAL = studentDAL;
    }

	@Override
	public int countStudents() {

		  return studentDAL.countStudents();
	}

	@Override
	public List<Double> calcAvgCGPA() {
		return studentDAL.calcAvgCGPA();

	}

   
}
