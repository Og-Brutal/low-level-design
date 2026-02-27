package com.apm.dto;

public class AuthorDTO {
    private int authorId;
    private String name;
    private String biography;

    // Constructors
    public AuthorDTO() {}

    public AuthorDTO(int authorId, String name, String biography) {
        this.authorId = authorId;
        this.name = name;
        this.biography = biography;
    }

    // Getters and Setters
    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public String toString() {
        return "AuthorDTO{" +
                "authorId=" + authorId +
                ", name='" + name + '\'' +
                ", biography='" + biography + '\'' +
                '}';
    }
}
