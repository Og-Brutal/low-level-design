package com.abft.dal;

public class MySQLDAOFactory extends AbstractDAOFactory {
    @Override
    public IBookDAO createBookDAO() {
        return new BookDAO();
    }

    @Override
    public IMemberDAO createMemberDAO() {
        return new MemberDAO();
    }
}
