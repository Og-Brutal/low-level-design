package com.abft.bll;

import com.abft.dal.*;

public class MemberBO {
    private IDALFacade dalFacade;

    public MemberBO(IDALFacade dalFacade) {
        this.dalFacade = dalFacade;
    }

    public void addMember(Member member) {
        dalFacade.addMember(member);
    }

    public Member getMember(int id) {
        return dalFacade.getMemberById(id);
    }
}
