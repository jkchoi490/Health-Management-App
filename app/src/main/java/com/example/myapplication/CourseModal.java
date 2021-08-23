package com.example.myapplication;

public class CourseModal {

    // variables for our name,
    // description, tracks and duration, id.
    private String name;
    private String gender;
    private String height;
    private String exercise; //평균운동량
    private int id;

    // creating getter and setter methods
    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getgender() {
        return gender;
    }

    public void setgender(String gender) {
        this.gender = gender;
    }

    public String getheight() {
        return height;
    }

    public void setheight(String height) {
        this.height = height;
    }

    public String getexercise() {
        return exercise;
    }

    public void setexercise(String exercise) {
        this.exercise = exercise;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // constructor
    public CourseModal(String name, String gender, String height, String exercise) {
        this.name = name;
        this.gender = gender;
        this.height = height;
        this.exercise = exercise;
    }
}