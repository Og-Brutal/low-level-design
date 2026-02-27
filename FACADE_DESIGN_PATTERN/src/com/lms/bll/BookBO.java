 package com.lms.bll;
import com.lms.dal.IDataAccessLayer;
import com.lms.dto.Book;

public class BookBO implements IBook {
    private IDataAccessLayer repoLayer;

    public BookBO(IDataAccessLayer repoLayer) {
        this.repoLayer = repoLayer;
    }

    @Override
    public Book calculateFineForBook(String bookCode) {
        return null;
    }
}
