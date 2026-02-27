package com.lms.bll;
import com.lms.dal.IDataAccessLayer;
import com.lms.dto.Book;
import com.lms.dto.StudentRecord;

public class BusinessLayerFacade implements IBusinessLayer {
    private BookBO bookHandler;
    private StudentBO studentHandler;

    public BusinessLayerFacade(IDataAccessLayer repoLayer) {
        bookHandler = new BookBO(repoLayer);
        studentHandler = new StudentBO(repoLayer);
    }

    @Override
    public StudentRecord calculateTotalPenalty(String learnerId) {
        return studentHandler.calculateTotalPenalty(learnerId);
    }

    @Override
    public Book calculateFineForBook(String bookCode) {
        return bookHandler.calculateFineForBook(bookCode);
    }
}
	