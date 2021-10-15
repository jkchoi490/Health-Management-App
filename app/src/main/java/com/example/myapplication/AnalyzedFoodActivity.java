package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class AnalyzedFoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyzed_food);

        //분석한 식품 정보(map)내용 DB에 저장
        Intent intent_ = getIntent();
        Map<String, Integer> map = (Map<String, Integer>)intent_.getSerializableExtra("map");

        for (String mapkey : map.keySet()){
            System.out.println("key:"+mapkey+",value:"+map.get(mapkey));
        }

        System.out.println("탄수화물 값 "+map.get("탄수화물 "));
        System.out.println("단백질 값(value)"+map.get("단백질 ")); //이거 보여주면 되것다!


        //not_over Activity에 온경우
        //Intent intent_2 = getIntent();
        //Map<String, Integer> map_not_over = (Map<String, Integer>)intent_.getSerializableExtra("map_not_over");



    }
}