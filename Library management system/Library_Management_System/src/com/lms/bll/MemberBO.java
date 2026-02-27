package com.lms.bll;

import com.lms.dal.IMemberDAO;
import com.lms.dtl.MemberDTO;

public class MemberBO implements IMemberBO{
	private IMemberDAO memberDAO ; 
	
	 public MemberBO ( IMemberDAO memberDAO ) {
	 this . memberDAO = memberDAO ; 
	 }
	
	 public void addMember ( MemberDTO member ) {
	 memberDAO.addMember ( member ) ;
	 }
	
	 public MemberDTO getMember ( int id ) {
	 return memberDAO . getMemberById ( id ) ;
	 }
}
