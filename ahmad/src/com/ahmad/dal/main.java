package com.ahmad.dal;

public class main {

	public static void main(String[] args) {
		DataAccessLayerFacade obj=new DataAccessLayerFacade(new AuthorFileDAO(),new BookFileDAO());
		
		int state=-1;
		/*
		 * while(true) switch(state) { case -1: if(temp<des) state=1;// heating else
		 * if(temp>des) state=2;//colling break; case 1: //tempratue optization logic
		 * state=-1; case 2: //tempratue optization logic state=-1; case 3: askasjd }
		 */
	}

}
