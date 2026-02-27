package com.lms.dto;

public class MemberDTO {
    private String name;
    private String bio;

    // Default constructor
    public MemberDTO() {}

    // Parameterized constructor
    public MemberDTO(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String bio) {
        this.bio = bio;
    }

    // toString method
    @Override
    public String toString() {
        return "MemberDTO{" +
                "name='" + name + '\'' +
                ", Bio=" + bio +
                '}';
    }
}
