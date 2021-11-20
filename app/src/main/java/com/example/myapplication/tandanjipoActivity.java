package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class tandanjipoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tandanjipo);

        DBHandlerNutrition dbHelperN = new DBHandlerNutrition(this.getApplicationContext());
        ArrayList<CourseModalN> courseModalArrayList = dbHelperN.readCoursesN();

        for (CourseModalN row : courseModalArrayList) { // 데이터 읽음

            //계산된 칼로리 화면에 표시

            try {
                String cal_db = row.getCal();
                String tan_db = row.getTan();
                String dan_db = row.getDan();
                String ji_db = row.getJi();
                String poji_db = row.getPoji();

                System.out.println("칼로리 _______: " + cal_db);
                System.out.println("탄수 : " + tan_db);
                System.out.println("단백 : " + dan_db);
                System.out.println("지방 : " + ji_db);
                System.out.println("포화지방 : " + poji_db);


                TextView day_cal = findViewById(R.id.day_cal);
                day_cal.setText(cal_db);

                TextView day_tan = findViewById(R.id.day_tan);
                day_tan.setText(tan_db);

                TextView day_dan = findViewById(R.id.day_dan);
                day_dan.setText(dan_db);

                TextView day_ji = findViewById(R.id.day_ji);
                day_ji.setText(ji_db);

                TextView day_poji = findViewById(R.id.day_poji);
                day_poji.setText(poji_db);


            } catch (Exception err) {
                System.out.println("db에러 같은데");
            }
        }


        //Button
        Button button_tandanjipo= findViewById(R.id.button_tandanjipo);
        button_tandanjipo.setOnClickListener(v -> {
            Intent ok_intent= new Intent(tandanjipoActivity.this,tcolnadangActivity.class);
            startActivity(ok_intent);
        });

    }
}