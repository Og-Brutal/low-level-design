package com.lms.bll;

import com.lms.dao.IDALFacade;
import com.lms.dto.BookDTO;

public class BookBO implements IBookBO {
	IDALFacade DALFacade;
	
	public BookBO(IDALFacade DALFacade) {
		this.DALFacade=DALFacade;
	}
	@Override
	public BookDTO getBook() {
		return DALFacade.getBook();
	}

}
