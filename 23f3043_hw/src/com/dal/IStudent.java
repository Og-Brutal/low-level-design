package com.dal;

import java.util.List;

public interface IStudent {
    int countStudents();
    List<Double> calcAvgCGPA();  // returns list of CGPAs
}
