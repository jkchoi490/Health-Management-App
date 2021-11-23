package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DialogNoExerciseWarningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_no_exercise_warning);


        Button button_exercise= findViewById(R.id.button_exercise);
        button_exercise.setOnClickListener(v -> {
            Intent i_back = new Intent(DialogNoExerciseWarningActivity.this, ExerciseActivity.class);
            startActivity(i_back);
        });




    }
}