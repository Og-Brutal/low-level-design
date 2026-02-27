package com.abft.dal;

public interface IDAOFactory {
    IBookDAO createBookDAO();
    IMemberDAO createMemberDAO();
}
