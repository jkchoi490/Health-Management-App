package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Insert2Activity extends AppCompatActivity {

    private EditText NameEdt, HeightEdt, GenderEdt, ExerciseEdt;
    private Button addCourseBtn;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert2);

        NameEdt = findViewById(R.id.idEdtName); //이름
        HeightEdt = findViewById(R.id.idEdtHeight); //키
        GenderEdt = findViewById(R.id.idEdtGender);// 성별
        ExerciseEdt = findViewById(R.id.idEdtExercise); //운동량
        addCourseBtn = findViewById(R.id.idBtnAddCourse);
        // readCourseBtn = findViewById(R.id.idBtnReadCourse);
        dbHandler = new DBHandler(Insert2Activity.this);

        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Name = NameEdt.getText().toString();
                String Height = HeightEdt.getText().toString();
                String Gender = GenderEdt.getText().toString();
                String Exercise = ExerciseEdt.getText().toString();

                if (Name.isEmpty() && Height.isEmpty() && Gender.isEmpty() && Exercise.isEmpty()) {
                    Toast.makeText(Insert2Activity.this, "여백이있습니다 모두 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHandler.addNewCourse(Name, Gender, Exercise, Height);

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