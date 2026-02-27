package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.SentenceDTO;

public class SentenceBO implements ISentenceBO{
	private IDataAccessLayerFasade daf;
	private ITokenBO tokenBO;
	
	
	public SentenceBO(IDataAccessLayerFasade daf,ITokenBO tokenBO)
	{
		this.daf = daf;
		this.tokenBO=tokenBO;
	}

	@Override
	public boolean createSentence(String chapterName, String text, String textDiacritized, String translation,String notes) {
		int chapterID=daf.searchChapter(chapterName);
		if(chapterID==-1) {
			return false;
		}
		boolean check=daf.createSentence(chapterID, text, textDiacritized, translation, notes);
		if(check) {
			tokenBO.addToken(text);
		}
		return check;
	}

	@Override
	public ArrayList<SentenceDTO> retrieveSentence(String chapterName) {
		int chapterID=daf.searchChapter(chapterName);
		if(chapterID==-1) {
			return null;
		}
		return daf.retrieveSentence(chapterID);
	}

	@Override
	public boolean updateSenetence(String chapterName, int sentenceNumber, String text, String textDiacritized,String translation, String notes) {
		int chapterID=daf.searchChapter(chapterName);
		if(chapterID==-1) {
			return false;
		}
		SentenceDTO sentence=daf.sentenceByNumbers(chapterID, sentenceNumber);
		boolean istokenDeleted=tokenBO.deleteTokensBySentence(sentence.getText());
		boolean check=daf.updateSenetence(chapterID, sentenceNumber, text, textDiacritized, translation, notes);
		if(check) {
			tokenBO.addToken(text);
		}
		return check;
	}

	@Override
	public boolean deleteSentence(String chapterName, int sentenceNumber) {
		int chapterID=daf.searchChapter(chapterName);
		if(chapterID==-1) {
			return false;
		}
		return daf.deleteSentence(chapterID, sentenceNumber);
	}

	@Override
	public int searchSentence(String text) {
		return daf.searchSentence(text);
	}

	@Override
	public SentenceDTO sentenceByNumbers(String chapterName, int sentenceNumber) {
		int chapterID=daf.searchChapter(chapterName);
		if(chapterID!=-1) {
			return daf.sentenceByNumbers(chapterID, sentenceNumber);
		}
		return null;
	}

	@Override
	public ArrayList<String> getSentencesByToken(String token) {
		return daf.getSentencesByToken(token);
	}

	@Override
	public ArrayList<String> getSentencesByTokenPattern(String tokenPattern) {
		return daf.getSentencesByTokenPattern(tokenPattern);
	}



	
}
