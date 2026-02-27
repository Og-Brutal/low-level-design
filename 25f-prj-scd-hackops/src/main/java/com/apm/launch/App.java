package main.java.com.apm.launch;

import java.sql.Connection;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.apm.dal.DBConnectionDBC;
import com.apm.dal.INgramDAO;
import com.apm.observers.IObserver;
import com.apm.pl.SplashScreen;

import main.java.com.apm.bll.AuthorBO;
import main.java.com.apm.bll.BookBO;
import main.java.com.apm.bll.BussinessLayerFasade;
import main.java.com.apm.bll.ChapterBO;
import main.java.com.apm.bll.LemmaBO;
import main.java.com.apm.bll.RootBO;
import main.java.com.apm.bll.SegmentedTokenBO;
import main.java.com.apm.bll.SentenceBO;
import main.java.com.apm.bll.TokenBO;
import main.java.com.apm.dal.AuthorDAO;
import main.java.com.apm.dal.BookDAO;
import main.java.com.apm.dal.ChapterDAO;
import main.java.com.apm.dal.DataAccessLayerFasade;
import main.java.com.apm.dal.LemmaDAO;
import main.java.com.apm.dal.NgramDAO;
import main.java.com.apm.dal.RootDAO;
import main.java.com.apm.dal.SegmentedTokenDAO;
import main.java.com.apm.dal.SentenceDAO;
import main.java.com.apm.dal.TextExtracter;
import main.java.com.apm.dal.TokenDAO;
import main.java.com.apm.pl.Arabic_Prose_User_Interface;

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
	    // 🟢 Initialize NgramDAO
	    INgramDAO ngramDAO = null;

	    
	        bookDAO = new BookDAO(conn);
	        authorDAO = new AuthorDAO(conn);
	        sentenceDAO = new SentenceDAO(conn);
	        chapterDAO = new ChapterDAO(conn);
	        tokenDAO = new TokenDAO(conn);
	        lemmaDAO = new LemmaDAO(conn);
	        rootDAO = new RootDAO(conn);
	        segmentedTokenDAO = new SegmentedTokenDAO(conn);
	        
	        // 🟢 Create NgramDAO instance
	        ngramDAO = new NgramDAO(conn);

	    

	    // Step 3 & 4: Initialize Facades (Must happen before UI)
	    // 🟢 UPDATED: ngramDAO moved to 3rd argument to match your constructor
	    DataAccessLayerFasade daf = new DataAccessLayerFasade(
	            bookDAO, 
	            authorDAO, 
	            ngramDAO,        // Moved here based on your request
	            sentenceDAO, 
	            chapterDAO, 
	            textExtracter, 
	            tokenDAO, 
	            lemmaDAO, 
	            rootDAO, 
	            segmentedTokenDAO
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