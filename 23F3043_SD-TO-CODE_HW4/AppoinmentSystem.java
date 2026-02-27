package com.hw4;

public class AppoinmentSystem {
	private BillingSystem billing;
	private Doctor doctor;
	private Patient patient;
	
	public boolean scheduleAppointment(int patientid,int doctorid,String date) {
		boolean appointed=false;
		if(billing.checkPendingBills(patientid)==0) {
			
			if(doctor.checkAvailability(doctorid,date)) {
				appointed=patient.confirmAppointment(doctorid,date);
			}
		}
		return appointed;
		
	}

}

class BillingSystem{
	public int checkPendingBills(int patientid) {
		int pendingBills = 0;
		
		return pendingBills;
	}
}

class Doctor{
	public boolean checkAvailability(int doctorid,String date) {
		boolean available=false;
		return available;
	}
}

class Patient{
	public boolean confirmAppointment(int doctorid,String date) {
		return true;
	}
}
