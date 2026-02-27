package com.apm.bll;

import java.util.ArrayList;

import com.apm.dto.RootDTO;

public interface IRootBO {
	public boolean addRoots(ArrayList<String> roots);
	public int searchRoot(String text);
	public RootDTO getRoot(String text);
	public ArrayList<RootDTO> getAllRoots();
	public ArrayList<String> getAllRootsByBook(String bookName);
}
