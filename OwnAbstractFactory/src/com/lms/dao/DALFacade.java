package com.lms.dao;

import com.lms.dto.BookDTO;
import com.lms.dto.MemberDTO;

public class DALFacade implements IDALFacade{
	private IBookDAO bookDAO;
	private IMemberDAO MemberDAO;
	
	public DALFacade(IBookDAO bookDAO,IMemberDAO MemberDAO) {
		this.bookDAO=bookDAO;
		this.MemberDAO=MemberDAO;
	}
	

	@Override
	public BookDTO getBook() {
		return bookDAO.getBook();
	}

	@Override
	public MemberDTO getMemeber() {
		return MemberDAO.getMemeber();
	}
	
}
