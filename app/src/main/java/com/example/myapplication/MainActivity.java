package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        Button button_camera = findViewById(R.id.button_camera);
        button_camera.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity2.class);
            startActivity(intent);
        });
*/

        //영양성분표 촬영
        ImageButton button_camera = (ImageButton)findViewById(R.id.imageButtonCamera);
        button_camera.setOnClickListener(v -> {
           // Intent intent1 = new Intent(MainActivity.this, DetectorActivity.class);
            Intent intent1 = new Intent(MainActivity.this, SelectFoodActivity.class);
            startActivity(intent1);
        });

        //영양성분
        ImageButton button_chart= (ImageButton)findViewById(R.id.imageButtonChart);
        button_chart.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity.this,NutritionActivity.class);
            startActivity(intent2);
        });

        //식단관리
        ImageButton button_food = (ImageButton)findViewById(R.id.imageButtonFood);
        button_food.setOnClickListener(v -> {
            Intent intent3 = new Intent(MainActivity.this, FoodActivity.class);
            startActivity(intent3);
        });

        //마이페이지
        ImageButton button_manage = (ImageButton)findViewById(R.id.imageButtonManage);
        button_manage.setOnClickListener(v -> {
            Intent intent4 = new Intent(MainActivity.this, ViewCourses.class);
            startActivity(intent4);
        });

        //test 용
        ImageButton button_camera_analyze = (ImageButton)findViewById(R.id.imageButtonAnalyze);
        button_camera_analyze.setOnClickListener(v -> {
            Intent intent5 = new Intent(MainActivity.this, AnalyzedFoodActivity.class);
            startActivity(intent5);
        });

        ImageButton button_exercise = (ImageButton)findViewById(R.id.imageButtonJogging);
        button_exercise.setOnClickListener(v -> {
            Intent intent_exercise = new Intent(MainActivity.this, ExerciseActivity.class);
            startActivity(intent_exercise);
        });

        ImageButton button_calendar = findViewById(R.id.imageButtonCalandar);
        button_calendar.setOnClickListener(v -> {
            Intent intent_calendar = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent_calendar);
        });

    }
}