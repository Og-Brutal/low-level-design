package com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.SentenceDTO;

public interface ISentenceDAO {
	public boolean createSentence(int chapterID, String text, String textDiacritized,String translation, String notes);
	public ArrayList<SentenceDTO> retrieveSentence(int chapterID);
	public boolean updateSenetence(int chapterID, int sentenceNumber,String text, String textDiacritized, String translation, String notes);
	public boolean deleteSentence(int chapterID,int sentenceNumber);
	public int searchSentence(String text);
}
