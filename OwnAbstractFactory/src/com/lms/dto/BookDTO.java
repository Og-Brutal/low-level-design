package com.lms.dto;

public class BookDTO {
    private String name;
    private String era;

    // Constructor
    public BookDTO(String name, String era) {
        this.name = name;
        this.era = era;
    }

    // Default constructor
    public BookDTO() {}

    // Getters
    public String getName() {
        return name;
    }

    public String getEra() {
        return era;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEra(String era) {
        this.era = era;
    }

    // toString method (optional, useful for debugging)
    @Override
    public String toString() {
        return "BookDTO{" +
                "name='" + name + '\'' +
                ", Era='" + era + '\'' +
                '}';
    }
}
