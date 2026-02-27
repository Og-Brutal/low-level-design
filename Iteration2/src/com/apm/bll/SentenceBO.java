package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.SentenceDTO;

public class SentenceBO implements ISentenceBO{
	private IDataAccessLayerFasade daf;
	
	
	public SentenceBO(IDataAccessLayerFasade daf)
	{
		this.daf = daf;
	}

	@Override
	public boolean createSentence(String chapterName, String text, String textDiacritized, String translation,String notes) {
		int chapterID=daf.searchChapter(chapterName);
		if(chapterID==-1) {
			return false;
		}
		return daf.createSentence(chapterID, text, textDiacritized, translation, notes);
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
		return daf.updateSenetence(chapterID, sentenceNumber, text, textDiacritized, translation, notes);
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

	
}
