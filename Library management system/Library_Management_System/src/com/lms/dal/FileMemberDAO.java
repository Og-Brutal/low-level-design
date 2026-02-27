package com.lms.dal;

import java.io.*;

import com.lms.dtl.MemberDTO;

public class FileMemberDAO implements IMemberDAO {
    private String filePath;

    public FileMemberDAO(String filePath) {
        this.filePath = filePath;
    }

    @Override
	public void addMember(MemberDTO member) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public MemberDTO getMemberById(int id) {
      
        return null;
    }

	
}
