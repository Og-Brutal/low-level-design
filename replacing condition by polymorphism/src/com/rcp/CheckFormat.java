package com.rcp;

public class CheckFormat {
	public boolean checkFormat(String format) {
		format+=' ';
		System.out.println(format);
		boolean isMatch=true;
		
		State s1=new AlphaState();
		
		char[] charArray=format.toCharArray();
		
		for(int i=0;i<charArray.length;i++) {
//			if(i==0) {
//				if(charArray[i]=='@'){
//					isMatch=false;
//					break;
//				}
//			}
//			if(i==charArray.length-1) {
//				break;
//			}
			
			
			s1=s1.Action(charArray[i]);
			
			
			
			
			
//			System.out.println("checked "+charArray[i]);
//			System.out.println(s1);
//			if(s1==null) {
//				isMatch=false;
//				break;
//			}
		}
		return isMatch;
	}
	
}
