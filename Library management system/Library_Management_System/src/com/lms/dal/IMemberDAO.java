package com.lms.dal;

import java.lang.reflect.Member;

import com.lms.dtl.MemberDTO;

public interface IMemberDAO {
 void addMember(MemberDTO member ) ;
 MemberDTO getMemberById ( int id ) ;
 }