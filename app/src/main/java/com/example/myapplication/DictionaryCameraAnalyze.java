package com.example.myapplication;

public class DictionaryCameraAnalyze {
    private String id; // 1,2,3 등 숫자
    private String nut_name; //영양성분 이름
    private String nut_size; // 영양성분 g

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNut_name() {
        return nut_name;
    }

    public void setNut_name(String nut_name) {
        this.nut_name = nut_name;
    }

    public String getNut_size() {
        return nut_size;
    }

    public void setNut_size(String nut_size) {
        this.nut_size = nut_size;
    }

    public DictionaryCameraAnalyze(String id, String nut_name, String nut_size) {
        this.id = id;
        this.nut_name = nut_name;
        this.nut_size = nut_size;
    }
}
