package com.lms.dal;

public class FileDAOFactory extends AbstractDAOFactory {
    @Override
    public IBookDAO createBookDAO() {
        return new FileBookDAO("books.txt");
    }

    @Override
    public IMemberDAO createMemberDAO() {
        return new FileMemberDAO("members.txt");
    }
}
