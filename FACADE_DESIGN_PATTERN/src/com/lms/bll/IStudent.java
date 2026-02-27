package com.lms.bll;
import com.lms.dto.StudentRecord;

public interface IStudent {
    StudentRecord calculateTotalPenalty(String learnerId);
}
