package main.java.com.apm.bll;

import java.util.ArrayList;

import com.apm.bll.utils.LemmaUtil;
import com.apm.bll.utils.RootUtil;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.LemmaDTO;

public class LemmaBO implements ILemmaBO {
	private IDataAccessLayerFasade daf;
	private RootBO rootBO;
	
	public LemmaBO(IDataAccessLayerFasade daf,RootBO rootBO)
	{
		this.daf = daf;
		this.rootBO=rootBO;
	}

	@Override
	public boolean addLemmas(ArrayList<String> tokens) {
		boolean inserted=false;
		ArrayList<String> lemmas=LemmaUtil.getLemmaList(tokens);
		if(rootBO.addRoots(lemmas)) {
			inserted=true;
			for(String lemma :lemmas) {
				String root=RootUtil.getThreeLetterRoot(lemma);
				int rootID=daf.searchRoot(root);
				if(rootID!=-1) {
					daf.addLemmas(rootID, lemma);
				}
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

	@Override
	public ArrayList<String> getAllLemmasByBook(String bookName) {
		int bookId=daf.searchBook(bookName);
		if(bookId==-1) {
			return null;
		}
		return daf.getAllLemmasByBook(bookId);
	}

}
