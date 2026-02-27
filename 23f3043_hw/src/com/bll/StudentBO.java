package com.bll;

import com.dal.IDALFacade;
import java.util.List;

public class StudentBO implements IStudentBO {

    // ✅ Dependency Injection of DAL layer
    private IDALFacade dalFacade;

    // ✅ Constructor Injection
    public StudentBO(IDALFacade dalFacade) {
        this.dalFacade = dalFacade;
    }

    @Override
    public int countStudents() {
        return dalFacade.countStudents();
    }

    @Override
    public double calcAvgCGPA() {
        List<Double> cgpaList = dalFacade.calcAvgCGPA();
        if (cgpaList == null || cgpaList.isEmpty()) {
            return 0.0; // no data, return 0
        }

        double sum = 0.0;
        for (double cgpa : cgpaList) {
            sum += cgpa;
        }

        return sum / cgpaList.size(); // ✅ average CGPA
    }
}
