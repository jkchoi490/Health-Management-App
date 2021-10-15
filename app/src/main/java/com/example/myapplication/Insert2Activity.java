package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Insert2Activity extends AppCompatActivity {

    // creating variables for our edittext, button and dbhandler
    private EditText NameEdt, HeightEdt, GenderEdt, ExerciseEdt;
    private Button addCourseBtn;// readCourseBtn;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert2);

        // initializing all our variables.
        NameEdt = findViewById(R.id.idEdtName); //이름
        HeightEdt = findViewById(R.id.idEdtHeight); //키
        GenderEdt = findViewById(R.id.idEdtGender);// 성별
        ExerciseEdt = findViewById(R.id.idEdtExercise); //운동량
        addCourseBtn = findViewById(R.id.idBtnAddCourse);
        // readCourseBtn = findViewById(R.id.idBtnReadCourse);

        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = new DBHandler(Insert2Activity.this);

        // below line is to add on click listener for our add course button.
        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // below line is to get data from all edit text fields.
                String Name = NameEdt.getText().toString();
                String Height = HeightEdt.getText().toString();
                String Gender = GenderEdt.getText().toString();
                String Exercise = ExerciseEdt.getText().toString();

                // validating if the text fields are empty or not.
                if (Name.isEmpty() && Height.isEmpty() && Gender.isEmpty() && Exercise.isEmpty()) {
                    Toast.makeText(Insert2Activity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                dbHandler.addNewCourse(Name, Gender, Exercise, Height);

                // after adding the data we are displaying a toast message.
                Toast.makeText(Insert2Activity.this, "사용자 정보가 입력되었습니다", Toast.LENGTH_SHORT).show();
                NameEdt.setText("");
                GenderEdt.setText("");
                HeightEdt.setText("");
                ExerciseEdt.setText("");

                Intent i = new Intent(Insert2Activity.this, CalculateCaloriesActivity.class);
                startActivity(i);
            }
        });


    }
}