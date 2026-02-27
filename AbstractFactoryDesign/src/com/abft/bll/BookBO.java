package com.abft.bll;

import com.abft.dal.*;

public class BookBO {
    private IDALFacade dalFacade;

    public BookBO(IDALFacade dalFacade) {
        this.dalFacade = dalFacade;
    }

    public void addBook(Book book) {
        dalFacade.addBook(book);
    }

    public Book getBook(int id) {
        return dalFacade.getBookById(id);
    }
}
