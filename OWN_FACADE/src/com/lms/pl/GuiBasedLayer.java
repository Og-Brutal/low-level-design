package com.lms.pl;

import com.lms.bll.IBusinessFacade;

public class GuiBasedLayer implements IUserInterface{
	IBusinessFacade bll;
	
	public GuiBasedLayer() {}
	
	public GuiBasedLayer(IBusinessFacade bll) {
		this.bll=bll;
	}
	
	@Override
	public void show() {
		bll.getBook(null);
	}
	
	

}
