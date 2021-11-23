package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

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


        Intent intent_ = getIntent();

        ArrayList passList = intent_.getParcelableArrayListExtra("value_count");
        //   ArrayList<Par> items = getIntent().getParcelableArrayListExtra("value_count");

        //탄단지포....영양성분들 중에 하루 권장량을 넘어가면 x 이미지로 변경해주기
        try {
            /*
            data_tan = map.get("탄수화물 ");
            data_dan = map.get("단백질 ");
            data_ji = map.get("지방 ");
            data_poji = map.get("포화지방 ");
            data_sik = map.get("식이섬유 ");
            data_na = map.get("나트륨 ");
            data_col = map.get("콜레스테롤 ");
            */

            if (passList.contains("tan") == true) {
                tan_check.setImageResource(R.drawable.cancel);
            }
            if (passList.contains("dan") == true) {
                dan_check.setImageResource(R.drawable.cancel);
            }
            if (passList.contains("ji") == true) {
                ji_check.setImageResource(R.drawable.cancel);
            }
            if (passList.contains("poji") == true) {
                poji_check.setImageResource(R.drawable.cancel);
            }
            if (passList.contains("sik") == true) {
                sik_check.setImageResource(R.drawable.cancel);
            }
            if (passList.contains("na") == true) {
                na_check.setImageResource(R.drawable.cancel);
            }
            if (passList.contains("col") == true) {
                col_check.setImageResource(R.drawable.cancel);
            }

        }catch (Exception err){
            System.out.println("에러발생");
        }


        //운동하고 해당식품 먹기 버튼 클릭 시 dialog_ask_plan 으로 넘어가기
        Button button_yellow= findViewById(R.id.button_yellow);
        button_yellow.setOnClickListener(v -> {
            Intent ask_i = new Intent(DialogOverActivity.this, DialogAskPlanActivity.class);
            //ask_i.putExtra("map",(Serializable) map);
            startActivity(ask_i);
        });
        //취소 버튼시 돌아가기 기능
        Button button_cancel= findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(v -> {
            Intent cancel_i = new Intent(DialogOverActivity.this, MyNutritionsActivity.class);
            startActivity(cancel_i);
        });


    }
}