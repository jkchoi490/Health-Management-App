package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FoodActivity extends AppCompatActivity {

    private EditText courseNameEdt, courseTracksEdt, courseDurationEdt, courseDescriptionEdt;
    private Button addCourseBtn,readCourseBtn;
    private DBHandler2 dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        courseNameEdt = findViewById(R.id.idTVCourseName);
        courseTracksEdt = findViewById(R.id.idTVCourseTracks);
        courseDurationEdt = findViewById(R.id.idTVCourseDuration);
        courseDescriptionEdt = findViewById(R.id.idTVCourseDescription);
        addCourseBtn = findViewById(R.id.idBtnAddCourse);
        readCourseBtn =findViewById(R.id.idBtnReadCourse);


        dbHandler = new DBHandler2(FoodActivity.this);

        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String courseName = courseNameEdt.getText().toString();
                String courseTracks = courseTracksEdt.getText().toString();
                String courseDuration = courseDurationEdt.getText().toString();
                String courseDescription = courseDescriptionEdt.getText().toString();

                if (courseName.isEmpty() && courseTracks.isEmpty() && courseDuration.isEmpty() && courseDescription.isEmpty()) {
                    Toast.makeText(FoodActivity.this, "데이터를 입력중입니다..", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHandler.addNewCourse(courseName, courseDuration, courseDescription, courseTracks);

                Toast.makeText(FoodActivity.this, "데이터가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                courseNameEdt.setText("");
                courseDurationEdt.setText("");
                courseTracksEdt.setText("");
                courseDescriptionEdt.setText("");
            }

        });

        readCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i21 = new Intent(FoodActivity.this, ViewCourses2.class);
                startActivity(i21);
            }
        });


    }
}
