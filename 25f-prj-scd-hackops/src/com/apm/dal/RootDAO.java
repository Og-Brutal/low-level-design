package com.apm.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.apm.dto.RootDTO;

public class RootDAO implements IRootDAO {

    private Connection conn;
    private Statement stmt;

    public RootDAO(Connection conn, Statement stmt) {
        this.conn = conn;
        try {
            this.stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------ ADD ROOT ------------------------------
    @Override
    public boolean addRoots(String text) {
        try {
            String sql = "INSERT INTO Root (root) VALUES (?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, text);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ------------------------------ SEARCH ROOT ------------------------------
    @Override
    public int searchRoot(String text) {
        try {
            String sql = "SELECT root_id FROM Root WHERE root = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, text);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("root_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // not found
    }

    // ------------------------------ GET ROOT BY TEXT ------------------------------
    @Override
    public RootDTO getRoot(String text) {
        try {
            String sql = "SELECT root_id, root FROM Root WHERE root = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, text);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                RootDTO dto = new RootDTO();
                dto.setRootId(rs.getInt("root_id"));
                dto.setRoot(rs.getString("root"));
                return dto;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // not found
    }

    // ------------------------------ GET ALL ROOTS ------------------------------
    @Override
    public ArrayList<RootDTO> getAllRoots() {
        ArrayList<RootDTO> roots = new ArrayList<>();
        try {
            String sql = "SELECT root_id, root FROM Root";
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RootDTO dto = new RootDTO();
                dto.setRootId(rs.getInt("root_id"));
                dto.setRoot(rs.getString("root"));

                roots.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roots;
    }
    @Override
    public ArrayList<String> getAllRootsByBook(int bookId) {
        ArrayList<String> roots = new ArrayList<>();

        String sql =
            "SELECT DISTINCT r.root " +
            "FROM Root r " +
            "INNER JOIN Lemma l ON r.root_id = l.root_id " +
            "INNER JOIN Token t ON l.lemma_id = t.lemma_id " +
            "INNER JOIN Sentence s ON t.sentence_id = s.sentence_id " +
            "INNER JOIN Chapter c ON s.chapter_id = c.chapter_id " +
            "WHERE c.book_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roots.add(rs.getString("root"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return roots;
    }

}
