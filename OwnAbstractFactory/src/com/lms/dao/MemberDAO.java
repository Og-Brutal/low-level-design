package com.lms.dao;

import com.lms.dto.MemberDTO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDAO implements IMemberDAO {

    private static final String URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12802733?useSSL=false&serverTimezone=UTC";
    private static final String USER = "sql12802733";
    private static final String PASSWORD = "qtlZHRLtFJ";

    @Override
    public MemberDTO getMemeber() {
        MemberDTO member = null;

        String query = "SELECT name, biography FROM Author LIMIT 1"; // adjust table/column names as needed

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                // Create MemberDTO object using parameterized constructor
                member = new MemberDTO(
                    rs.getString("name"),
                    rs.getString("biography")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return member;
    }
}
