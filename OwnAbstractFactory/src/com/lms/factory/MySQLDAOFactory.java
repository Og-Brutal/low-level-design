package com.lms.factory;

import com.lms.dao.BookDAO;
import com.lms.dao.IBookDAO;
import com.lms.dao.IMemberDAO;
import com.lms.dao.MemberDAO;

public class MySQLDAOFactory extends AbstractDAOFactory{

	@Override
	public IBookDAO instanstiateBookDAO() {
		return new BookDAO();
	}

	@Override
	public IMemberDAO instanstiateMemberDAO() {
		return new MemberDAO();
	}

}
