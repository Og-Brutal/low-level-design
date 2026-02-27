package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.TokenDTO;

public class TokenBO implements ITokenBO {
	private IDataAccessLayerFasade daf;
	private ILemmaBO lemmaBO;
	private ISegmentedTokenBO segmentedTokenBO; 
	
	public TokenBO(IDataAccessLayerFasade daf,ILemmaBO lemmaBO,ISegmentedTokenBO segmentedTokenBO)
	{
		this.daf = daf;
		this.lemmaBO=lemmaBO;
		this.segmentedTokenBO=segmentedTokenBO;
	}


	@Override
	public boolean addToken(String text) {
		boolean inserted =false;
		int sentenceID=daf.searchSentence(text);
		ArrayList<String> tokensList=TextProcessingUtil.analyzeAndSegmentSentence(text);
		if(lemmaBO.addLemmas(tokensList)) {
			inserted=true;
			for(String token:tokensList) {
				String lemma=TextProcessingUtil.getLemma(token);
				int lemmaID=daf.searchLemma(lemma);
				if(lemmaID!=-1) {
					int tokenID=daf.addToken(sentenceID,lemmaID , token);
					segmentedTokenBO.addSegments(tokenID, token);
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
			ArrayList<Integer> tokenIDs=daf.deleteTokensBySentence(sentenceID);
			segmentedTokenBO.deleteSegments(tokenIDs);
		}
		return false;
	}
	
}
