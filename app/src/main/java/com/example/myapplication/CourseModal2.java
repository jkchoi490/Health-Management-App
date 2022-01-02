package com.example.myapplication;

public class CourseModal2 {

    private String date; //date 날짜
    private String diet; //diet 아점저
    private String menu; //menu 메뉴이름
    private String size; //size 인분
    private int id;

    public String getdate() {
        return date;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public String getdiet() {
        return diet;
    }

    public void setdiet(String diet) {
        this.diet = diet;
    }

    public String getmenu() {
        return menu;
    }

    public void setmenu(String menu) {
        this.menu = menu;
    }

    public String getsize() {
        return size;
    }

    public void setsize(String size) {
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // constructor
    public CourseModal2(String date, String diet, String menu, String size) {
        this.date = date;
        this.diet = diet;
        this.menu = menu;
        this.size = size;
    }
}
