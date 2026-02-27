package com.lms.bll;

import com.lms.dto.BookDTO;
import com.lms.dto.MemberDTO;

public class BLLFacade implements IBLLFacade{
	private IBookBO bookBO;
	private IMemberBO memberBO;
	
	public BLLFacade(IBookBO bookBO,IMemberBO memberBO) {
		this.bookBO=bookBO;
		this.memberBO=memberBO;
	}
	@Override
	public BookDTO getBook() {
		return bookBO.getBook();
	}

	@Override
	public MemberDTO getMemeber() {
		// TODO Auto-generated method stub
		return memberBO.getMemeber();
	}

}
