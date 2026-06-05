package com.layered; 

public class Main {
	
	public static void main(String[] args) {
		FIleManager fm=new FIleManager();
		BusinessLogic bl=new BusinessLogic(fm);
		Gui view=new Gui(bl);
		view.setVisible(true);
	}
}
