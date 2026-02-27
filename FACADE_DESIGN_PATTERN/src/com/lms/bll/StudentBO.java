package com.lms.bll;
import com.lms.dal.IDataAccessLayer;
import com.lms.dto.StudentRecord;

public class StudentBO implements IStudent {
    private IDataAccessLayer repoLayer;

    public StudentBO(IDataAccessLayer repoLayer) {
        this.repoLayer = repoLayer;
    }

    @Override
    public StudentRecord calculateTotalPenalty(String learnerId) {
        return null;
    }
}
