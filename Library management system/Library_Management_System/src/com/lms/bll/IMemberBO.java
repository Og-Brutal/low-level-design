package com.lms.bll;

import com.lms.dtl.MemberDTO;

public interface IMemberBO {
	
	 public void addMember ( MemberDTO member );
	
	 public MemberDTO getMember ( int id );
}
