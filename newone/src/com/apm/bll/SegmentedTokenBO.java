package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.SegmentedTokenDTO;

public class SegmentedTokenBO implements ISegmentedTokenBO {
	private IDataAccessLayerFasade daf;
	
	
	public SegmentedTokenBO(IDataAccessLayerFasade daf)
	{
		this.daf = daf;
	}
	@Override
	public boolean addSegments(int tokenID, String token) {
		System.out.println("calling functions");
		String prefix=TextProcessingUtil.getPrefix(token);
		System.out.println(prefix);
		String stem=TextProcessingUtil.getStem(token);
		System.out.println(stem);
		String lemma=TextProcessingUtil.getLemma(token);
		System.out.println(lemma);
		String root=TextProcessingUtil.getThreeLetterRoot(lemma);
		System.out.println(root);
		return daf.addSegments(tokenID,prefix,stem,lemma,root);
	}
	
	@Override
	public ArrayList<SegmentedTokenDTO> getAllSegments() {
		return daf.getAllSegments();
	}
	@Override
	public boolean deleteSegments(ArrayList<Integer> tokenIDs) {
	    if (tokenIDs == null || tokenIDs.isEmpty()) {
	        return false; // nothing to delete
	    }

	    boolean allDeleted = true;

	    for (Integer tokenID : tokenIDs) {
	        if (tokenID != null) {
	            boolean deleted = daf.deleteSegments(tokenID);
	            if (!deleted) {
	                allDeleted = false; // track failure
	            }
	        }
	    }

	    return allDeleted;
	}


	

}
