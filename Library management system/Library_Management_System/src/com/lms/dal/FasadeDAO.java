package com.lms.dal;

import java.lang.reflect.Member;

import com.lms.dtl.BookDTO;
import com.lms.dtl.MemberDTO;

public class FasadeDAO implements IFasadeDAO{
	
	private IBookDAO book;
	private IMemberDAO member;
    public FasadeDAO(IBookDAO book,IMemberDAO member)
    {
    	this.book = book;
    	this.member = member;
    }
	@Override
	public void addBook(BookDTO book) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BookDTO getBookById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addMember(MemberDTO member) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MemberDTO getMemberById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
