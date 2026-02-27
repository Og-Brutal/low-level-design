package com.apm.app;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.apm.bll.AuthorBO;
import com.apm.bll.BookBO;
import com.apm.bll.BussinessLayerFasade;
import com.apm.bll.ChapterBO;
import com.apm.bll.SentenceBO;
import com.apm.dal.AuthorDAO;
import com.apm.dal.BookDAO;
import com.apm.dal.ChapterDAO;
import com.apm.dal.DBConnectionDBC;
import com.apm.dal.DataAccessLayerFasade;
import com.apm.dal.SentenceDAO;
import com.apm.pl.Arabic_Prose_User_Interface;


public class App {
	
	 public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		  Connection conn = DBConnectionDBC.getConnection();
		  BookDAO bookDAO = null; // initialize with null so we can access it outside try catch
		try {
			bookDAO = new BookDAO(conn,conn.createStatement() );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  DataAccessLayerFasade daf=null;
		try {
			daf = new DataAccessLayerFasade(bookDAO,new AuthorDAO(conn,conn.createStatement()),new SentenceDAO(conn,conn.createStatement()),new ChapterDAO(conn,conn.createStatement()));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  BookBO bookBO = new BookBO (daf);
		  BussinessLayerFasade blf = new BussinessLayerFasade(bookBO,new AuthorBO(daf),new SentenceBO(daf),new ChapterBO(daf));
	        try {
	            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	        } catch (Exception e) {
	            // Fallback to default look and feel
	        }
	        System.out.println("showing ui");
	     // Apply theme protection first
	        
	        System.setProperty("swing.crossplatformlaf", "javax.swing.plaf.metal.MetalLookAndFeel");
	        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	        // Then safely start your UI
	        SwingUtilities.invokeLater(() -> new Arabic_Prose_User_Interface(blf));

	    }
}
