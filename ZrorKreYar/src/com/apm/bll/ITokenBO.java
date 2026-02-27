package com.apm.bll;

import java.util.ArrayList;

import com.apm.dto.SegmentedTokenDTO;
import com.apm.dto.TokenDTO;

public interface ITokenBO {
	public boolean addToken(String text);
	public ArrayList<TokenDTO> getTokensByLemma(String lemma); 
	public ArrayList<TokenDTO> getAllTokens();
	public boolean deleteTokensBySentence(String sentence);
	public String getTokenText(int tokenID);
	public SegmentedTokenDTO getSegmentedTokenDetails(String token);
	public ArrayList<String> getAllTokensByBook(String bookName);
}
