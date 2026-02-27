package com.lms.dtl;

public class MemberDTO {
   
    private int memberId;
    private String name;
    private String email;


    
    public MemberDTO() {
        // default constructor
    }

    public MemberDTO(int memberId, String name, String email, String phoneNumber, String address, String membershipType, boolean isActive) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
       
    }

   
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

   

    

  

    
    
}
