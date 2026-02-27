package com.ahmad.dal;

public class DataAccessLayerFacade implements IDataAccessLayerFacade {
	IAuthorDAO authorDAO;
	IBookDAO  bookDAO;

	public DataAccessLayerFacade(IAuthorDAO authorDAO,IBookDAO  bookDAO) {
		this.authorDAO=authorDAO;
		this.bookDAO=bookDAO;
	}



	@Override
	public void getAuthor() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void getbook() {
		// TODO Auto-generated method stub
		
	}

}
