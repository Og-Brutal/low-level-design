package com.lms.bll;

import com.lms.dtl.BookDTO;
import com.lms.dtl.MemberDTO;

public class FasadeBO implements IFasadeBO{
	
	private IBookBO book;
	private IMemberBO member;
	public FasadeBO(IBookBO book,IMemberBO member)
	{
		this.book = book;
		this.member = member;
	}
	@Override
	public void addBook(BookDTO book) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BookDTO getBook(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addMember(MemberDTO member) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MemberDTO getMember(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
