package com.abft.dal;

public class DALFacade implements IDALFacade {
    private IBookDAO bookDAO;
    private IMemberDAO memberDAO;

    public DALFacade(IBookDAO bookDAO, IMemberDAO memberDAO) {
        this.bookDAO = bookDAO;
        this.memberDAO = memberDAO;
    }

    @Override
    public void addBook(Book book) {
        bookDAO.addBook(book);
    }

    @Override
    public Book getBookById(int id) {
        return bookDAO.getBookById(id);
    }

    @Override
    public void addMember(Member member) {
        memberDAO.addMember(member);
    }

    @Override
    public Member getMemberById(int id) {
        return memberDAO.getMemberById(id);
    }
}
