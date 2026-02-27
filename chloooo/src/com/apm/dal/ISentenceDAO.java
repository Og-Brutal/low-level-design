package com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.SentenceDTO;

public interface ISentenceDAO {
	public boolean createSentence(int chapterID, String text, String textDiacritized,String translation, String notes);
	public ArrayList<SentenceDTO> retrieveSentence(int chapterID);
	public boolean updateSenetence(int chapterID, int sentenceNumber,String text, String textDiacritized, String translation, String notes);
	public boolean deleteSentence(int chapterID,int sentenceNumber);
	public int searchSentence(String text);
	public SentenceDTO sentenceByNumbers(int chapterNumber, int sentenceNumber) ;
	public ArrayList<String> getSentencesByToken(String token);
	public ArrayList<String> getSentencesByTokenPattern(String tokenPattern);
	public ArrayList<SentenceDTO> getSentencesByRoot(String rootText);
	public ArrayList<SentenceDTO> getSentencesByLemma(String lemmaText);
	ArrayList<String> getAllSentences();
}
