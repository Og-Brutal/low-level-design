package com.lms.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.lms.bll.*;
import com.lms.dao.*;
import com.lms.factory.AbstractDAOFactory;
import com.lms.pl.UserInterfacePL;

public class App {
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		
		IBookDAO bookDAO=AbstractDAOFactory.getInstance().instanstiateBookDAO();
		
		IMemberDAO memberDAO=AbstractDAOFactory.getInstance().instanstiateMemberDAO();
		
		IDALFacade DALFacade=new DALFacade(bookDAO,memberDAO);
		
		IBookBO bookBO=new BookBO(DALFacade);
		IMemberBO memberBO=new MemberBO(DALFacade);
		
		IBLLFacade BLLFacade=new BLLFacade(bookBO,memberBO);
		
		UserInterfacePL PLInterface=new UserInterfacePL(BLLFacade);
		
		
		PLInterface.showLibrary();
		
		
		
		
	}
}
