package com.apm.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.apm.bll.AuthorBO;
import com.apm.bll.BookBO;
import com.apm.bll.BussinessLayerFasade;
import com.apm.bll.ChapterBO;
import com.apm.bll.LemmaBO;
import com.apm.bll.RootBO;
import com.apm.bll.SegmentedTokenBO;
import com.apm.bll.SentenceBO;
import com.apm.bll.TokenBO;
import com.apm.dal.AuthorDAO;
import com.apm.dal.BookDAO;
import com.apm.dal.ChapterDAO;
import com.apm.dal.DBConnectionDBC;
import com.apm.dal.DataAccessLayerFasade;
import com.apm.dal.LemmaDAO;
import com.apm.dal.RootDAO;
import com.apm.dal.SegmentedTokenDAO;
import com.apm.dal.SentenceDAO;
import com.apm.dal.TextExtracter;
import com.apm.dal.TokenDAO;
import com.apm.observers.IObserver;
import com.apm.pl.Arabic_Prose_User_Interface;
import com.apm.pl.SplashScreen; // Import the SplashScreen class

public class App {

	public static void main(String[] args) {
	    // Step 1: Establish DB Connection (Must be done first)
	    Connection conn = DBConnectionDBC.getConnection();
	    if (conn == null) {
	        JOptionPane.showMessageDialog(null,
	                "Failed to connect to the database. Application will exit.",
	                "Database Error", JOptionPane.ERROR_MESSAGE);
	        System.exit(1);
	    }

	    ArrayList<IObserver> observers = new ArrayList<>();

	    // Step 2: Initialize DAOs (with proper exception handling)
	    BookDAO bookDAO = null;
	    AuthorDAO authorDAO = null;
	    SentenceDAO sentenceDAO = null;
	    ChapterDAO chapterDAO = null;
	    TokenDAO tokenDAO = null;
	    LemmaDAO lemmaDAO = null;
	    RootDAO rootDAO = null;
	    TextExtracter textExtracter = new TextExtracter();
	    SegmentedTokenDAO segmentedTokenDAO = null;

	    try {
	        bookDAO = new BookDAO(conn, conn.createStatement());
	        authorDAO = new AuthorDAO(conn, conn.createStatement());
	        sentenceDAO = new SentenceDAO(conn, conn.createStatement());
	        chapterDAO = new ChapterDAO(conn, conn.createStatement());
	        tokenDAO = new TokenDAO(conn, conn.createStatement());
	        lemmaDAO = new LemmaDAO(conn, conn.createStatement());
	        rootDAO = new RootDAO(conn, conn.createStatement());
	        segmentedTokenDAO = new SegmentedTokenDAO(conn, conn.createStatement());

	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null,
	                "Error initializing database access objects: " + e.getMessage(),
	                "DAO Initialization Error", JOptionPane.ERROR_MESSAGE);
	        System.exit(1);
	    }

	    // Step 3 & 4: Initialize Facades (Must happen before UI)
	    DataAccessLayerFasade daf = new DataAccessLayerFasade(
	            bookDAO, authorDAO, sentenceDAO, chapterDAO, textExtracter, tokenDAO, lemmaDAO, rootDAO, segmentedTokenDAO
	    );

	    RootBO rootBO = new RootBO(daf);
	    LemmaBO lemmaBO = new LemmaBO(daf, rootBO);
	    SegmentedTokenBO segmentedTokenBO = new SegmentedTokenBO(daf);
	    TokenBO tokenBO = new TokenBO(daf, lemmaBO, segmentedTokenBO);
	    SentenceBO sentenceBO = new SentenceBO(daf, tokenBO, observers);
	    ChapterBO chapterBO = new ChapterBO(daf, sentenceBO, observers);
	    BookBO bookBO = new BookBO(daf, chapterBO, observers);
	    AuthorBO authorBO = new AuthorBO(daf);

	    // Step 5: Set Look and Feel (System L&F for modern native feel)
	    try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception ex) {
	        System.err.println("Warning: Failed to set system Look and Feel. Using default.");
	        ex.printStackTrace();
	    }

	    // Step 6: Launch Splash Screen and Main UI on Event Dispatch Thread
	    SwingUtilities.invokeLater(() -> {
	        // 1. Create the Splash Screen
	        SplashScreen splash = new SplashScreen();

	        // 2. Start the Splash Screen with a callback
	        splash.startSplash(() -> {
	            try {
	                // 3. Create the main UI object FIRST
	                Arabic_Prose_User_Interface ui = new Arabic_Prose_User_Interface(
	                        new BussinessLayerFasade(
	                                bookBO, authorBO, sentenceBO, chapterBO, tokenBO, lemmaBO, rootBO, segmentedTokenBO
	                        )
	                );

	                // 4. Add UI as observer to relevant BOs
	                bookBO.addObserver(ui);
	                chapterBO.addObserver(ui);
	                sentenceBO.addObserver(ui);

	                // 5. Make UI visible
	                ui.setVisible(true);

	            } catch (Exception e) {
	                e.printStackTrace();
	                JOptionPane.showMessageDialog(null,
	                        "Failed to launch the application UI: " + e.getMessage(),
	                        "UI Launch Error", JOptionPane.ERROR_MESSAGE);
	            }
	        });
	    });
	}

}