package com.apm.bll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.TokenDTO;

public class TokenBO implements ITokenBO {
	private IDataAccessLayerFasade daf;
	
	
	public TokenBO(IDataAccessLayerFasade daf)
	{
		this.daf = daf;
	}


	@Override
	public boolean addToken(String text, ArrayList<String> tokens) {
		boolean inserted =false;
		int sentenceID=daf.searchSentence(text);
		if(sentenceID!=-1) {
			inserted=true;
			for(String token :tokens) {
				int lemmaID;
					lemmaID = daf.searchLemma(TextProcessingUtil.getLemma(token));
					if(lemmaID!=-1) {
						daf.addToken(sentenceID, lemmaID, token);
					}
			}
		}
		return inserted;
	}


	@Override
	public ArrayList<TokenDTO> getTokensByLemma(String lemma) {
		ArrayList<TokenDTO> tokenList=null;
		int lemmaID=daf.searchLemma(lemma);
		if(lemmaID!=-1) {
			tokenList=daf.getTokensByLemma(lemmaID);
		}
		return tokenList;
	}


	@Override
	public ArrayList<TokenDTO> getAllTokens() {
		return daf.getAllTokens();
	}


	@Override
	public boolean deleteTokensBySentence(String sentence) {
		int sentenceID=daf.searchSentence(sentence);
		if(sentenceID!=-1) {
			return daf.deleteTokensBySentence(sentenceID);
		}
		return false;
	}
	
}
