package com.hw4;

public class RegisterOffice {
	private  AccountsReceivable ar;
	private  Drama drama;
	public boolean registerStudent(int studentid) {
		if(ar.getPastDueBalance(studentid)==0) {
			return drama.addStudent(studentid);
		}
		return false;
	}
	
}


class AccountsReceivable{
	public double getPastDueBalance(int studentid) {
		double past_due_balance=0;
		return past_due_balance;
	}
}


class Drama{
	public boolean addStudent(int studentid) {
		return true;
	}
}