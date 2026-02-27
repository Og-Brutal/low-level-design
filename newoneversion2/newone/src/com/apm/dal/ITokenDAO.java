package com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.TokenDTO;

public interface ITokenDAO {
	public boolean addToken(int sentenceID,int lemmaID,String text);
	public ArrayList<TokenDTO> getTokensByLemma(int lemmaID); 
	public ArrayList<TokenDTO> getAllTokens();
	public boolean deleteTokensBySentence(int sentenceID);
}
