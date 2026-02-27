package com.apm.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.apm.dto.LemmaDTO;
import java.sql.Statement;

public class LemmaDAO implements ILemmaDAO {

	private Connection conn;
    private Statement stmt;

    public LemmaDAO(Connection conn, Statement stmt) {
        this.conn = conn;
        try {
            this.stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------ ADD LEMMA ------------------------------
    @Override
    public boolean addLemmas(int rootID, String text) {
        try {
            String sql = "INSERT INTO Lemma (root_id, lemma) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, rootID);
            ps.setString(2, text);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ------------------------------ SEARCH LEMMA ------------------------------
    @Override
    public int searchLemma(String text) {
        try {
            String sql = "SELECT lemma_id FROM Lemma WHERE lemma = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, text);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("lemma_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // not found
    }

    // ------------------------------ GET LEMMA BY ROOT ------------------------------
    @Override
    public ArrayList<LemmaDTO> getLemmaByRoot(int rootID) {
        ArrayList<LemmaDTO> lemmas = new ArrayList<>();
        try {
            String sql = "SELECT lemma_id, root_id, lemma FROM Lemma WHERE root_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, rootID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LemmaDTO dto = new LemmaDTO();
                dto.setLemmaId(rs.getInt("lemma_id"));
                dto.setRootId(rs.getInt("root_id"));
                dto.setLemma(rs.getString("lemma"));

                lemmas.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lemmas; // returns empty list if no lemmas found for this root
    }

    // ------------------------------ GET ALL LEMMAS ------------------------------
    @Override
    public ArrayList<LemmaDTO> getAllLemmas() {
        ArrayList<LemmaDTO> lemmas = new ArrayList<>();
        try {
            String sql = "SELECT lemma_id, root_id, lemma FROM Lemma";
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LemmaDTO dto = new LemmaDTO();
                dto.setLemmaId(rs.getInt("lemma_id"));
                dto.setRootId(rs.getInt("root_id"));
                dto.setLemma(rs.getString("lemma"));

                lemmas.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lemmas;
    }
    @Override
    public ArrayList<String> getAllLemmasByBook(int bookId) {
        ArrayList<String> lemmas = new ArrayList<>();

        String sql =
            "SELECT DISTINCT l.lemma " +
            "FROM Lemma l " +
            "INNER JOIN Token t ON l.lemma_id = t.lemma_id " +
            "INNER JOIN Sentence s ON t.sentence_id = s.sentence_id " +
            "INNER JOIN Chapter c ON s.chapter_id = c.chapter_id " +
            "WHERE c.book_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lemmas.add(rs.getString("lemma"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return lemmas;
    }


}
