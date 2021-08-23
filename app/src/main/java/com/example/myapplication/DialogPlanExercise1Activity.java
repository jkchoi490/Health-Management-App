package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DialogPlanExercise1Activity extends AppCompatActivity {

    private Spinner spinner;
    ArrayList spinnerList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;


    private EditText edit_hour, edit_minutes; //운동시간 입력

    ArrayList<String> s_valueList = new ArrayList<String>(); //운동종류, 시, 분 액티비티 전달할 리스트트

    ArrayList spinnerList_value = new ArrayList<>();

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_plan_exercise1);


       spinnerList.add("자전거");
       spinnerList.add("체조");
       spinnerList.add("조금 빨리 걷기");
       spinnerList.add("배드민턴");
       spinnerList.add("골프");
        spinnerList.add("야구");
        spinnerList.add("달리기");
        spinnerList.add("웨이트 트레이닝");
        spinnerList.add("농구");
        spinnerList.add("수영");
        spinnerList.add("조깅");
        spinnerList.add("축구");
        spinnerList.add("테니스");
        spinnerList.add("등산");
        spinnerList.add("사이클");
        spinnerList.add("수영");
        spinnerList.add("런닝");
        spinnerList.add("계단오르기");

        // "3", "3.5","3.8","4.5","4.5","5","5","6",
       //               "6","6","7","7","7","7.5","8","8","10","15"
       spinnerList_value.add("3");
       spinnerList_value.add("3.5");
       spinnerList_value.add("3.8");
       spinnerList_value.add("4.5");
       spinnerList_value.add("4.5");
       spinnerList_value.add("5");
       spinnerList_value.add("5");
       spinnerList_value.add("6");
       spinnerList_value.add("6");
       spinnerList_value.add("6");
       spinnerList_value.add("7");
       spinnerList_value.add("7");
       spinnerList_value.add("7");
       spinnerList_value.add("7.5");
       spinnerList_value.add("8");
       spinnerList_value.add("8");
       spinnerList_value.add("10");
       spinnerList_value.add("15");



        edit_hour = (EditText)findViewById(R.id.hour);
        edit_minutes =(EditText)findViewById(R.id.minutes);




        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, spinnerList);
        spinner = (Spinner)findViewById(R.id.spinner_exercise);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                Toast.makeText(getApplicationContext(),spinnerList.get(i)+"이(가) 선택되었습니다.", Toast.LENGTH_SHORT).show();
                s_valueList.add((String) spinnerList.get(i)); //선택한 운동 넣기
                s_valueList.add((String) spinnerList_value.get(i));
              //  String value = (String) spinnerList_value.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //운동 진행시 칼로리 소모량 계산하기
        Button button_orange= (Button)findViewById(R.id.button_orange);
        button_orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orange_i = new Intent(DialogPlanExercise1Activity.this, DialogPlanExercise2Activity.class);
                String s_hour= edit_hour.getText().toString(); //운동하는 시간 - 시
                String s_minutes = edit_minutes.getText().toString(); //운동하는시간 - 분

                s_valueList.add(s_hour);
                s_valueList.add(s_minutes);

                orange_i.putExtra("s_valueList",s_valueList);
                startActivity(orange_i);
            }
        });

       Button button_pre= (Button)findViewById(R.id.button_pre);
       button_pre.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent pre_i = new Intent(DialogPlanExercise1Activity.this, DialogAskPlanActivity.class);
               startActivity(pre_i);
               edit_hour.setText("");
               edit_minutes.setText("");
           }
       });


    }
}