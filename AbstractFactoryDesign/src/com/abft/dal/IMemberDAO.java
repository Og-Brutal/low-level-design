package com.abft.dal;

public interface IMemberDAO {
    void addMember(Member member);
    Member getMemberById(int id);
}
