package com.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Student implements IStudent {

    private Connection connection;

    // ✅ Dependency Injection via constructor
    public Student(Connection connection) {
        this.connection = connection;
    }

    // ✅ Count total students
    @Override
    public int countStudents() {
        int count = 0;
        String query = "SELECT COUNT(*) AS total FROM student";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error counting students:");
            e.printStackTrace();
        }
        return count;
    }

    // ✅ Return list of all CGPAs
    @Override
    public List<Double> calcAvgCGPA() {
        List<Double> cgpaList = new ArrayList<>();
        String query = "SELECT cgpa FROM student";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cgpaList.add(rs.getDouble("cgpa"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching CGPAs:");
            e.printStackTrace();
        }

        return cgpaList;
    }
}
