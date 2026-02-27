package com.example.bll;

import com.example.dal.StudentDAL;
import com.example.model.Student;

import java.util.List;
import java.util.Objects;

public class StudentBLL {
    private final StudentDAL dal;

    public StudentBLL(StudentDAL dal) {
        this.dal = Objects.requireNonNull(dal, "DAL cannot be null");
    }

    public int countStudents() {
        List<Student> students = dal.fetchAllStudents();
        if (students == null) {
            return 0;
        }
        return students.size();
    }

    public double calcAvgCGPA() {
        List<Student> students = dal.fetchAllStudents();
        if (students == null || students.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (Student s : students) {
            if (s == null) continue;
            double cgpa = s.getCgpa();
            if (cgpa < 0.0 || cgpa > 4.0) {
                throw new IllegalArgumentException("Invalid CGPA value: " + cgpa);
            }
            sum += cgpa;
        }
        return sum / students.size();
    }
}
