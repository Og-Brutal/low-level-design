package com.lms.factory;

import com.lms.dao.FileBookDAO;
import com.lms.dao.FileMemberDAO;
import com.lms.dao.IBookDAO;
import com.lms.dao.IMemberDAO;

public class FileDAOFactory extends AbstractDAOFactory{

	@Override
	public IBookDAO instanstiateBookDAO() {
		return new FileBookDAO();
	}

	@Override
	public IMemberDAO instanstiateMemberDAO() {
		return new FileMemberDAO();
	}

}
