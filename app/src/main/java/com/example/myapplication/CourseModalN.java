package com.example.myapplication;

public class CourseModalN {

    private String name;
    private String gender;
    private String height;
    private String exercise; //평균운동량
    private int id; //0

    private String cal;
    private String tan;
    private String dan;
    private String ji;
    private String poji;
    private String sik;
    private String col;
    private String na;

    public String getname() { return name; }

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
//-------------탄단지포 식콜나
    public String getCal() { return cal; }

    public void setCal(String cal) {
        this.cal = cal;
    }


    public String getTan() { return tan; }

    public void setTan(String tan) {
        this.tan = tan;
    }

    public String getDan() { return dan; }

    public void setDan(String dan) {
        this.dan = dan;
    }

    public String getJi() { return ji; }

    public void setJi(String ji) {
        this.ji = ji;
    }

    public String getPoji() { return poji; }

    public void setPoji(String poji) {
        this.poji = poji;
    }

    public String getSik() { return sik; }

    public void setSik(String sik) { this.sik = sik; }

    public String getCol() { return col; }

    public void setCol(String col) { this.col = col; }

    public String getNa() { return na; }

    public void setNa(String na) { this.na = na; }

    // constructor
    public CourseModalN(String name, String gender, String height, String exercise,String cal,
                        String tan, String dan, String ji, String poji, String sik,
                        String col, String na) {
        this.name = name;
        this.gender = gender;
        this.height = height;
        this.exercise = exercise;
        this.cal = cal;
        this.tan = tan;
        this.dan = dan;
        this.ji = ji;
        this.poji = poji;
        this.sik = sik;
        this.col = col;
        this.na = na;
    }
}
