package com.apm.bll;

import java.util.ArrayList;

import com.apm.dto.TokenDTO;

public interface ITokenBO {
	public boolean addToken(String text);
	public int searchToken(String text);
	public TokenDTO getToken(String text);
	public ArrayList<TokenDTO> getAllTokens();
}
