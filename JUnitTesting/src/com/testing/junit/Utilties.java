package com.testing.junit;

public class Utilties {
	
	public boolean isLowerCase(String str) {
		if(str==null) return true;
		boolean check=true;	
		for(int i=0;i<str.length();i++) {
			if(str.charAt(i)>='A' && str.charAt(i)<='Z') {
				check=false;
			}
		}
		return check;
	}
}
