package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.TokenDTO;

public class TokenBO implements ITokenBO {
	private IDataAccessLayerFasade daf;
	
	
	public TokenBO(IDataAccessLayerFasade daf)
	{
		this.daf = daf;
	}
	@Override
	public boolean addToken(String text) {
		boolean inserted=false;
		int sentenceid=daf.searchSentence(text);
		if(sentenceid!=-1) {
			inserted=true;
			ArrayList<String> tokens=TextProcessingUtil.sentenceToTokens(text);
			for(String token :tokens) {
				
				if(!daf.addToken(sentenceid,token)) {
					inserted=false;
					break;
				}
			}
		}
		return inserted;
	}

	@Override
	public int searchToken(String text) {
		return daf.searchToken(text);
	}

	@Override
	public TokenDTO getToken(String text) {
		return daf.getToken(text);
	}

	@Override
	public ArrayList<TokenDTO> getAllTokens() {
		return daf.getAllTokens();
	}

}
