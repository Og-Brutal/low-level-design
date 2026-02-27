package com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.TokenDTO;

public interface ITokenDAO {
	public boolean addToken(int sentenceID,String text);
	public int searchToken(String text);
	public TokenDTO getToken(String text);
	public ArrayList<TokenDTO> getAllTokens();
}
