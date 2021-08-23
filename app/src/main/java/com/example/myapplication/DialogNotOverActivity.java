package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DialogNotOverActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_not_over);
/*
        Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.checked, null);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        edit.setCompoundDrawables(drawable, null, null, null);
     //   editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checked, 0);
*/
        Button button_cancel= (Button)findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_i = new Intent(DialogNotOverActivity.this, CameraAnalyzeActivity.class);
                startActivity(back_i); //일단 DialogNotActivity로 이동
            }
        });

        Button button_save =(Button)findViewById(R.id.button_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DialogNotOverActivity.this, "구매/분석한 식품에 해당 식품이 저장되었습니다", Toast.LENGTH_SHORT).show();

                //구매/분석한 식품 DB를 생성해서 분석이 완료된식품을 저장할 수 있도록 함

            }

        });

    }
}