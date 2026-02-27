package main.java.com.apm.bll;

import java.util.ArrayList;

import com.apm.dto.SentenceDTO;
import com.apm.dto.SimilarityResultDTO;

public interface ISentenceBO {
	public boolean createSentence(String chapterName,String text, String textDiacritized, String translation, String notes);
	public ArrayList<SentenceDTO> retrieveSentence(String bookName);
	public boolean updateSenetence(String bookName, int sentenceNumber,String text, String textDiacritized, String translation, String notes);
	public boolean deleteSentence(String bookName,int sentenceNumber);
	public int searchSentence(String text);
	public SentenceDTO sentenceByNumbers(String chapterName,int sentenceNumber);
	public ArrayList<String> getSentencesByToken(String token);
	public ArrayList<SentenceDTO> getSentencesByRoot(String rootText);
	public ArrayList<String> getSentencesByTokenPattern(String tokenPattern);
	public double checkSimilarity(String s1,String s2);
	ArrayList<SimilarityResultDTO> findSimilarSentences(String text, double threshold);
}
