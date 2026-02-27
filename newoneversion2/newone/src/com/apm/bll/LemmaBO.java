package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.LemmaDTO;

public class LemmaBO implements ILemmaBO {
	private IDataAccessLayerFasade daf;
	
	
	public LemmaBO(IDataAccessLayerFasade daf)
	{
		this.daf = daf;
	}

	@Override
	public boolean addLemmas(ArrayList<String> Lemmas) {
		boolean inserted=true;
		for(String lemma : Lemmas) {
			int rootID=daf.searchRoot(TextProcessingUtil.getThreeLetterRoot(lemma));
			if(rootID!=-1) {
				daf.addLemmas(rootID, lemma);
			}
		}
		return inserted;
	}

	@Override
	public int searchLemma(String text) {
		return daf.searchLemma(text);
	}

	@Override
	public ArrayList<LemmaDTO> getLemmaByRoot(String root) {
		ArrayList<LemmaDTO> lemmaList=null;
		int rootID=daf.searchRoot(root);
		if(rootID!=-1) {
			lemmaList=daf.getLemmaByRoot(rootID);
		}
		return lemmaList;
	}

	@Override
	public ArrayList<LemmaDTO> getAllLemmas() {
		return daf.getAllLemmas();
	}

}
