package com.lms.pl;

import com.lms.bll.IBusinessFacade;

public class CliBasedLayer implements IUserInterface{
	
	IBusinessFacade bll;
	
	public CliBasedLayer() {}
	
	public CliBasedLayer(IBusinessFacade bll) {
		this.bll=bll;
	}
	
	@Override
	public void show() {}

}
