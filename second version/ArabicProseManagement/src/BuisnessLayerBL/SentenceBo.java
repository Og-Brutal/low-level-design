package BuisnessLayerBL;

public class SentenceBo {
	 public SentenceBO() {
	        this.sentenceDAO = new SentenceDAO();
	    }
	 
	    public boolean addSentence(Sentence sentence) {
	        if (sentence == null || sentence.getText() == null || sentence.getText().trim().isEmpty()) {
	            System.err.println("Sentence text cannot be empty!");
	            return false;
	        }
	        if (sentence.getBookId() <= 0) {
	            System.err.println("Valid book must be selected!");
	            return false;
	        }
	        if (sentence.getSentenceNumber() <= 0) {
	            System.err.println("Sentence number must be positive!");
	            return false;
	        }
	        return sentenceDAO.insertSentence(sentence);
	    }
	    
	    public List<Sentence> getAllSentences() {
	        return sentenceDAO.getAllSentences();
	    }
	    
	    public Sentence getSentenceById(int sentenceId) {
	        if (sentenceId <= 0) {
	            System.err.println("Invalid sentence ID!");
	            return null;
	        }
	        return sentenceDAO.getSentenceById(sentenceId);
	    }
	    
	  
	    
	    public boolean updateSentence(Sentence sentence) {
	        if (sentence == null || sentence.getSentenceId() <= 0) {
	            System.err.println("Invalid sentence data!");
	            return false;
	        }
	        if (sentence.getText() == null || sentence.getText().trim().isEmpty()) {
	            System.err.println("Sentence text cannot be empty!");
	            return false;
	        }
	        if (sentence.getBookId() <= 0) {
	            System.err.println("Valid book must be selected!");
	            return false;
	        }
	        if (sentence.getSentenceNumber() <= 0) {
	            System.err.println("Sentence number must be positive!");
	            return false;
	        }
	        return sentenceDAO.updateSentence(sentence);
	    }
	    
	    public boolean deleteSentence(int sentenceId) {
	        if (sentenceId <= 0) {
	            System.err.println("Invalid sentence ID!");
	            return false;
	        }
	        return sentenceDAO.deleteSentence(sentenceId);
	    }
	    
	    public List<Sentence> searchSentences(String keyword) {
	        if (keyword == null || keyword.trim().isEmpty()) {
	            return getAllSentences();
	        }
	        return sentenceDAO.searchSentences(keyword);
	    }
	}
}
