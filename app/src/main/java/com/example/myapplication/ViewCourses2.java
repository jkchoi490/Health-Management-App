package com.example.myapplication;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewCourses2 extends AppCompatActivity {

    private ArrayList<CourseModal2> courseModalArrayList;
    private DBHandler2 dbHandler;
    private CourseRVAdapter2 courseRVAdapter;
    private RecyclerView coursesRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_courses2);
        courseModalArrayList = new ArrayList<>();
        dbHandler = new DBHandler2(ViewCourses2.this);

        courseModalArrayList = dbHandler.readCourses();

        courseRVAdapter = new CourseRVAdapter2(courseModalArrayList, ViewCourses2.this);
        coursesRV = findViewById(R.id.idRVCourses);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewCourses2.this, RecyclerView.VERTICAL, false);
        coursesRV.setLayoutManager(linearLayoutManager);
        coursesRV.setAdapter(courseRVAdapter);
    }
}
