package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.Map;

public class DialogAskPlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ask_plan);

        Intent intent_dap = getIntent();
        Map<String, Integer> map = (Map<String, Integer>)intent_dap.getSerializableExtra("map");

        //예 버튼 클릭 시
        Button button_yes= findViewById(R.id.button_yes);
        button_yes.setOnClickListener(v -> {
           Intent plan_yes = new Intent(DialogAskPlanActivity.this, DialogPlanExercise1Activity.class);
           plan_yes.putExtra("map",(Serializable) map);
           startActivity(plan_yes);
        });

        //아니오 버튼 클릭 시
        Button button_no= findViewById(R.id.button_no);
        button_no.setOnClickListener(v -> {
            Intent plan_no = new Intent(DialogAskPlanActivity.this, DialogNoExerciseWarningActivity.class);
            plan_no.putExtra("map",(Serializable) map);
            startActivity(plan_no);
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