package com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.LemmaDTO;
import com.apm.dto.TokenDTO;

public interface ILemmaDAO {
	public boolean addLemmas(int rootID,String text);
	public int searchLemma(String text);
	public ArrayList<LemmaDTO> getLemmaByRoot(int rootID);
	public ArrayList<LemmaDTO> getAllLemmas();
}
