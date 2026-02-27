package com.example.model;

public class Student {
    private int id;
    private String rollNo;
    private String name;
    private double cgpa;

    public Student(int id, String rollNo, String name, double cgpa) {
        this.id = id;
        this.rollNo = rollNo;
        this.name = name;
        this.cgpa = cgpa;
    }

    public int getId() { return id; }
    public String getRollNo() { return rollNo; }
    public String getName() { return name; }
    public double getCgpa() { return cgpa; }

    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
}
