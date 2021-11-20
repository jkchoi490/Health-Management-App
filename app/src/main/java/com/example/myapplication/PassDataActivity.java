package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PassDataActivity extends AppCompatActivity {

    private Bitmap pass_faceBmp = null;
    private String pass_name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_data);

        Intent i = getIntent();
        pass_faceBmp = i. getParcelableExtra("pass_image");
        ImageView passFace = findViewById(R.id.pass_dlg_image);
        passFace.setImageBitmap(pass_faceBmp);


    }
}