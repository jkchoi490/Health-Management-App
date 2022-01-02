package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.Map;

public class DialogNotOverActivity extends AppCompatActivity {

    Intent intent_ = getIntent();
    Map<String, Integer> map_not_over = (Map<String, Integer>)intent_.getSerializableExtra("map");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_not_over);

        Button button_cancel= (Button)findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(v -> {
            Intent back_i = new Intent(DialogNotOverActivity.this, MainActivity.class);// 추가
            startActivity(back_i); //일단 DialogNotActivity로 이동
        });

        Button button_save =(Button)findViewById(R.id.button_save);
        button_save.setOnClickListener(v -> {
            Toast.makeText(DialogNotOverActivity.this, "구매/분석한 식품에 해당 식품이 저장되었습니다", Toast.LENGTH_SHORT).show();
            Intent save_i = new Intent(DialogNotOverActivity.this,AnalyzedFoodActivity.class);
            save_i.putExtra("map_not_over",(Serializable) map_not_over);

        });

    }
}