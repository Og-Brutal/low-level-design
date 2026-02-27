package main.java.com.apm.bll;

import java.util.ArrayList;

import com.apm.dto.SegmentedTokenDTO;

public interface ISegmentedTokenBO {
	public boolean addSegments(int tokenID,String token);
	public ArrayList<SegmentedTokenDTO> getAllSegments();
	public boolean deleteSegments(ArrayList<Integer> tokenIDs);
}
