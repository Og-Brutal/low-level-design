package com.hw4;

public class Library {
	private Accounts accounts;
	private Catalog catalog;
	private Member member;
	
	public boolean issueBook(int memberId,int bookId) {
		
		if(accounts.checkOverdueBooks(memberId)==0) {
			if(catalog.checkBookAvailability(bookId)) {
				return member.issueBook(bookId);
			}
		}
		return false; 
	}
}

class Accounts{
	public int checkOverdueBooks(int memberId) {
		int overDue=0;
		return overDue;
	}
}
class Catalog {
	public boolean checkBookAvailability(int bookId) {
		boolean avialable=true;
		return avialable;
	}
}

class Member{
	public boolean issueBook(int bookId) {
		return true;
	}
}