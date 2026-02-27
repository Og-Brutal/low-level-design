package com.apm.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.apm.dto.TokenDTO;

public class TokenDAO implements ITokenDAO {
	 private Connection conn;
	 private Statement stmt;
	 public TokenDAO( Connection conn,Statement stmt)
	 {	
		 this.conn = conn;
		 try {
			this.stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }
	 
	 
	 @Override
	 public boolean addToken(int sentenceID, String text) {
	     try {
	         String sql = "INSERT INTO Token (sentence_id, token) VALUES (?, ?)";
	         PreparedStatement ps = conn.prepareStatement(sql);

	         ps.setInt(1, sentenceID);
	         ps.setString(2, text);

	         int rows = ps.executeUpdate();
	         return rows > 0;

	     } catch (SQLException e) {
	         e.printStackTrace();
	         return false;
	     }
	 }


	 @Override
	 public int searchToken(String text) {
	     try {
	         String sql = "SELECT token_id FROM token WHERE Token = ?";
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ps.setString(1, text);

	         ResultSet rs = ps.executeQuery();

	         if (rs.next()) {
	             return rs.getInt("token_id");
	         }

	     } catch (SQLException e) {
	         e.printStackTrace();
	     }

	     return -1;  // not found
	 }

	 @Override
	 public TokenDTO getToken(String text) {
	     try {
	         String sql = "SELECT token_id, sentence_id, token FROM token WHERE Token = ?";
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ps.setString(1, text);

	         ResultSet rs = ps.executeQuery();

	         if (rs.next()) {
	             TokenDTO dto = new TokenDTO();
	             dto.setTokenId(rs.getInt("token_id"));
	             dto.setSentenceId(rs.getInt("sentence_id"));
	             dto.setToken(rs.getString("token"));
	             return dto;
	         }

	     } catch (SQLException e) {
	         e.printStackTrace();
	     }

	     return null;  // token not found
	 }
	 
	 @Override
	 public ArrayList<TokenDTO> getAllTokens() {
	     ArrayList<TokenDTO> tokens = new ArrayList<>();
	     try {
	         String sql = "SELECT token_id, sentence_id, token FROM Token";
	         PreparedStatement ps = conn.prepareStatement(sql);

	         ResultSet rs = ps.executeQuery();

	         while (rs.next()) {
	             TokenDTO dto = new TokenDTO();
	             dto.setTokenId(rs.getInt("token_id"));
	             dto.setSentenceId(rs.getInt("sentence_id"));
	             dto.setToken(rs.getString("token"));

	             tokens.add(dto);
	         }

	     } catch (SQLException e) {
	         e.printStackTrace();
	     }

	     return tokens;
	 }



}
