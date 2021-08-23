package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DialogPlanExercise2Activity extends AppCompatActivity {

    TextView kind_of_exercise,exercise_hour,exercise_min,calories_output,chuchen_g;
    double weight = 78; //db에서 받아오기로 변경
    double chuchen = 0; //권장섭취량

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_plan_exercise2);


        ArrayList<String> s_valueList = (ArrayList<String>) getIntent().getSerializableExtra("s_valueList");
        System.out.println("activity로 잘 이동 완료"+s_valueList);

        kind_of_exercise = (TextView) findViewById(R.id.kind_of_exercise);
        exercise_hour = (TextView) findViewById(R.id.exercise_hour);
        exercise_min = (TextView) findViewById(R.id.exercise_min);
        calories_output = (TextView) findViewById(R.id.calories_output);
        chuchen_g = (TextView) findViewById(R.id.chuchen_g);

        String sKind_of_exercise = s_valueList.get(2);//선택한 운동종류
        String s_exercise_hour= s_valueList.get(4);
        String s_exercise_min= s_valueList.get(5);

        int hour = Integer.parseInt(s_exercise_hour);
        int minutes = Integer.parseInt(s_exercise_min);
        double calories_outputs = 0; //칼로리소모량 계산

        double mets = 0;
        mets = Double.parseDouble(s_valueList.get(3));

        calories_outputs = ((mets*3.5*weight*(hour*60+minutes))/1000)*5;

        String s_calories_output= String.valueOf(calories_outputs);
        //String s_chuchen_g = chuchen_g.getText().toString();

        kind_of_exercise.setText(sKind_of_exercise);
        exercise_hour.setText(s_exercise_hour);
        exercise_min.setText(s_exercise_min);
        calories_output.setText(s_calories_output);
      //  chuchen_g.setText(s_chuchen_g);




    }
}