package com.lms.bll;

import com.lms.dal.IDataAccesLayerFacade;

public class StudentBO implements IStudentBO {
	private IDataAccesLayerFacade dal;
	
	public StudentBO() {}
	
	public StudentBO(IDataAccesLayerFacade dal) {
		this.dal=dal;
	}
	
	@Override
	public double getTotalFine(int studentId) {
		return dal.getTotalFine(studentId);
	}
	
}
