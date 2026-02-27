package com.lms.pl;

import com.lms.bll.IBLLFacade;

public class UserInterfacePL {
	private IBLLFacade BLLFacade;
	
	public UserInterfacePL(IBLLFacade BLLFacade) {
		this.BLLFacade=BLLFacade;
	}
	
	public void  showLibrary() {
		String book=BLLFacade.getBook().toString();
		String member=BLLFacade.getMemeber().toString();
		System.out.println(book);
		System.out.println("==============================");
		System.out.println(member);
		
	}
}
