package com.hw4;

public class FinancialAnalyst {
	private ReportingSystem system;
	
	public String[] getReport(int userId) {
		return system.getAvailableReports(userId);
	}
}


class ReportingSystem{
	private String clearance;
	private SecuritySystem secSystem;
	
	public String[] getAvailableReports(int userId) {
		clearance=secSystem.getSecurityClearance(userId);
		return determineAvailableReports(userId);
	}
	
	public String[] determineAvailableReports(int userid) {
		return new String[] {""};
	}
}

class SecuritySystem{
	public String getSecurityClearance(int userID) {
		return "";
	}
}