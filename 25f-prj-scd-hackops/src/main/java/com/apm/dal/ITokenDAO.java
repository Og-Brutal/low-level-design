package main.java.com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.TokenDTO;

public interface ITokenDAO {
	public int addToken(int sentenceID,int lemmaID,String text);
	public ArrayList<TokenDTO> getTokensByLemma(int lemmaID); 
	public ArrayList<TokenDTO> getAllTokens();
	public ArrayList<Integer>  deleteTokensBySentence(int sentenceID);
	public String getTokenText(int tokenID);
	public ArrayList<String> getAllTokensByBook(int bookId);
}
