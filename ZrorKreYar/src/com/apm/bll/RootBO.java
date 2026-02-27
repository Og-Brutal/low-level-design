package com.apm.bll;

import java.util.ArrayList;

import com.apm.bll.utils.RootUtil;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.RootDTO;

public class RootBO implements IRootBO {
	private IDataAccessLayerFasade daf;
	
	
	public RootBO(IDataAccessLayerFasade daf)
	{
		this.daf = daf;
	}
	@Override
	public boolean addRoots(ArrayList<String> lemmas) {
		boolean inserted =true;
		ArrayList<String> roots=RootUtil.getRootList(lemmas);
		for(String root :roots) {
			daf.addRoots(root);
		}
		return inserted;
	}

	@Override
	public int searchRoot(String text) {
		return daf.searchRoot(text);
	}

	@Override
	public RootDTO getRoot(String text) {
		return daf.getRoot(text);
	}

	@Override
	public ArrayList<RootDTO> getAllRoots() {
		return daf.getAllRoots();
	}
	@Override
	public ArrayList<String> getAllRootsByBook(String booName) {
		int bookId=daf.searchBook(booName);
		if(bookId==-1) {
			return null;
		}
		return daf.getAllRootsByBook(bookId);
	}

}
