package com.example.dal;

import com.example.model.Student;
import java.util.List;

public interface StudentDAL {
    List<Student> fetchAllStudents();
}
