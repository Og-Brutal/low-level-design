package com.lms.bll;

import com.lms.dao.IDALFacade;
import com.lms.dto.MemberDTO;

public class MemberBO implements IMemberBO {
	IDALFacade DALFacade;
	
	public MemberBO(IDALFacade DALFacade) {
		this.DALFacade=DALFacade;
	}

	@Override
	public MemberDTO getMemeber() {
		return DALFacade.getMemeber();
	}

}
