package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DialogAskPlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ask_plan);


        //예 버튼 클릭 시
        Button button_yes= (Button)findViewById(R.id.button_yes);
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent plan_i = new Intent(DialogAskPlanActivity.this, DialogPlanExercise1Activity.class);
               startActivity(plan_i);
            }
        });

        //아니오 버튼 클릭 시
        Button button_no= (Button)findViewById(R.id.button_no);
        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent plan_i = new Intent(DialogAskPlanActivity.this, DialogNoExerciseWarningActivity.class);
                startActivity(plan_i);
            }
        });

        //이전으로 버튼
        Button  button_cancel= (Button)findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent plan_i = new Intent(DialogAskPlanActivity.this, DialogOverActivity.class);
                startActivity(plan_i);
            }
        });



    }
}