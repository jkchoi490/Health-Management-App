package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class DialogOverActivity extends AppCompatActivity {




    //CameraAnalyzeActivity에서 넘어온 영양성분 값들
    int data_tan = 0;
    int data_dan = 0;
    int data_ji =  0;
    int data_poji = 0;
    int data_sik = 0;
    int data_na = 0;
    int data_col = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_over);


        ImageView tan_check = (ImageView)findViewById(R.id.imag_check_tan);
        ImageView dan_check = (ImageView)findViewById(R.id.imag_check_dan);
        ImageView ji_check = (ImageView)findViewById(R.id.imag_check_ji);
        ImageView poji_check = (ImageView)findViewById(R.id.imag_check_poji);
        ImageView sik_check = (ImageView)findViewById(R.id.imag_check_sik);
        ImageView na_check = (ImageView)findViewById(R.id.imag_check_na);
        ImageView col_check = (ImageView)findViewById(R.id.imag_check_col);


        Intent intent = getIntent();
        Map<String, Integer> map = (Map<String, Integer>)intent.getSerializableExtra("map");

        System.out.println("인텐트 값 전달하기 테스팅!!!!!!!---------------");
        for (String mapkey : map.keySet()){
            System.out.println("key:"+mapkey+",value:"+map.get(mapkey));
        }


        //탄단지포....영양성분들 중에 하루 권장량을 넘어가면 x 이미지로 변경해주기
        try {
            data_tan = map.get("탄수화물 ");
            data_dan = map.get("단백질 ");
            data_ji = map.get("지방 ");
            data_poji = map.get("포화지방 ");
            data_sik = map.get("식이섬유 ");
            data_na = map.get("나트륨 ");
            data_col = map.get("콜레스테롤 ");

            if (data_tan > 100) {
                tan_check.setImageResource(R.drawable.cancel);
            }
            if(data_dan >100){
                dan_check.setImageResource(R.drawable.cancel);
            }
            if(data_ji > 100){
                ji_check.setImageResource(R.drawable.cancel);
            }
            if(data_poji > 100){
                poji_check.setImageResource(R.drawable.cancel);
            }
            if(data_sik > 100){
                sik_check.setImageResource(R.drawable.cancel);
            }
            if(data_na > 100){
                na_check.setImageResource(R.drawable.cancel);
            }
             if(data_col > 100){
                col_check.setImageResource(R.drawable.cancel);
            }

        }catch (Exception err){
            System.out.println("에러발생");
        }


        //운동하고 해당식품 먹기 버튼 클릭 시 dialog_ask_plan 으로 넘어가기
        Button button_yellow= findViewById(R.id.button_yellow);
        button_yellow.setOnClickListener(v -> {
            Intent ask_i = new Intent(DialogOverActivity.this, DialogAskPlanActivity.class);
            startActivity(ask_i);
        });
        //취소 버튼시 돌아가기 기능
        Button button_cancel= findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(v -> {
            Intent cancel_i = new Intent(DialogOverActivity.this, CameraAnalyzeActivity.class);
            startActivity(cancel_i);
        });


    }
}