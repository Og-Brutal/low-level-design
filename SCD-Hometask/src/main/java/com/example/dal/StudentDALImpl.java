package com.example.dal;

import com.example.model.Student;
import java.util.ArrayList;
import java.util.List;

public class StudentDALImpl implements StudentDAL {

    private boolean dbConnected = false;

    public void connect() {
        dbConnected = true;
        System.out.println("[DAL] Database connected (mock).");
    }

    public void disconnect() {
        dbConnected = false;
        System.out.println("[DAL] Database disconnected (mock).");
    }

    @Override
    public List<Student> fetchAllStudents() {
        if (!dbConnected) {
            throw new IllegalStateException("Database not connected!");
        }
        List<Student> list = new ArrayList<>();
        list.add(new Student(1, "R001", "Ali", 3.4));
        list.add(new Student(2, "R002", "Sara", 3.8));
        return list;
    }
}
