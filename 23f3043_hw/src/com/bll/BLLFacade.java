package com.bll;

public class BLLFacade implements IBLLFacade {

    // ✅ Dependency Injection of Business Object
    private IStudentBO studentBO;

    // ✅ Constructor Injection
    public BLLFacade(IStudentBO studentBO) {
        this.studentBO = studentBO;
    }

    @Override
    public int countStudents() {
        // Delegate to business object
        return studentBO.countStudents();
    }

    @Override
    public double calcAvgCGPA() {
        // Delegate to business object
        return studentBO.calcAvgCGPA();
    }
}
