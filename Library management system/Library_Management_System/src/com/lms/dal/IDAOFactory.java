package com.lms.dal;

public interface IDAOFactory {
    IBookDAO createBookDAO();
    IMemberDAO createMemberDAO();
}
