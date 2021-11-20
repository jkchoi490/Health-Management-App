package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SikActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sik);

        Button button_sik= findViewById(R.id.button_sik);
        button_sik.setOnClickListener(v -> {
            Intent imsick2 = new Intent(SikActivity.this,MainActivity.class);
            startActivity(imsick2);
        });
    }
}