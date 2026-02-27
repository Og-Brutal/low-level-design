package com.apm.bll;

import java.util.ArrayList;

import com.apm.dto.LemmaDTO;

public interface ILemmaBO {
	public boolean addLemmas(ArrayList<String> Lemma);
	public int searchLemma(String text);
	public ArrayList<LemmaDTO> getLemmaByRoot(String root);
	public ArrayList<LemmaDTO> getAllLemmas();
}
