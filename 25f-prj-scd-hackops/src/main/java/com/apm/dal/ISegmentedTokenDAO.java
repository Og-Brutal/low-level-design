package main.java.com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.SegmentedTokenDTO;

public interface ISegmentedTokenDAO {
	public boolean addSegments(int tokenID,String prefix,String stem,String lemma,String root);
	public ArrayList<SegmentedTokenDTO> getAllSegments();
	public boolean deleteSegments(int tokenID);
}
