package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class tcolnadangActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcolnadang);

        Button button_tcolna= findViewById(R.id.button_tcolna);
        button_tcolna.setOnClickListener(v -> {
            Intent imsick= new Intent(tcolnadangActivity.this,SikActivity.class);
            startActivity(imsick);
        });
    }
}