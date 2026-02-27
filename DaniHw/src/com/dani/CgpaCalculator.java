package com.dani;

import java.util.ArrayList;

public class CgpaCalculator {

    private StudentDataReader dataReader;

    public CgpaCalculator(StudentDataReader dataReader) {
        this.dataReader = dataReader;
    }

    public String calculateCgpaForStudent(String rollNumber) {
        ArrayList<Double> gpaList = dataReader.readGpaData(rollNumber);
        if (gpaList == null) {
            return "No GPA data available for this student";
        }
        double totalGpa = 0.0;
        for (double gpa : gpaList) {
            totalGpa += gpa;
        }
        return String.format("CGPA: %.2f", totalGpa / gpaList.size());
    }
}
