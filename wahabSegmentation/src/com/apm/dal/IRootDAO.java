package com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.LemmaDTO;
import com.apm.dto.RootDTO;

public interface IRootDAO {
	public boolean addRoots(String text);
	public int searchRoot(String text);
	public RootDTO getRoot(String text);
	public ArrayList<RootDTO> getAllRoots();
}
