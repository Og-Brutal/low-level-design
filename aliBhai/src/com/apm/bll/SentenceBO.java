package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.SentenceDTO;
import com.apm.observers.IObserveable;
import com.apm.observers.IObserver;

public class SentenceBO implements ISentenceBO,IObserveable{
	private IDataAccessLayerFasade daf;
	private ITokenBO tokenBO;
	private ArrayList<IObserver> observers;
	
	public SentenceBO(IDataAccessLayerFasade daf,ITokenBO tokenBO,ArrayList<IObserver> observers)
	{
		this.daf = daf;
		this.tokenBO=tokenBO;
		this.observers=observers;
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
			update();
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
			update();
		}
		return check;
	}

	@Override
	public boolean deleteSentence(String chapterName, int sentenceNumber) {
		int chapterID=daf.searchChapter(chapterName);
		if(chapterID==-1) {
			return false;
		}
		boolean check=daf.deleteSentence(chapterID, sentenceNumber);
		if(check) {
			update();
		}
		return check;
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

	@Override
	public boolean addObserver(IObserver observer) {
		this.observers.add(observer);
		return true;
	}

	@Override
	public boolean removeObserver() {
		return true;
	}

	@Override
	public void update() {
		for(IObserver observer :observers) {
			observer.autoRefresh("Sentence");
		}
		
	}

	@Override
	public ArrayList<SentenceDTO> getSentencesByRoot(String rootText) {
		return daf.getSentencesByRoot(rootText);
	}

	
}
