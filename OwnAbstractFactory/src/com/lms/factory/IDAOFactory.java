package com.lms.factory;

import com.lms.dao.IBookDAO;
import com.lms.dao.IMemberDAO;

public interface IDAOFactory {
	public IBookDAO instanstiateBookDAO();
	public IMemberDAO instanstiateMemberDAO();
}
