package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SelectFoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_food);

        Button button_snack= findViewById(R.id.button_snack);
        button_snack.setOnClickListener(v -> {
            Intent snack_i = new Intent(SelectFoodActivity.this, DetectorActivity.class);
            startActivity(snack_i);
        });

        Button button_ice = findViewById(R.id.button_ice);
        button_ice.setOnClickListener(v -> {
            Intent ice_i = new Intent(SelectFoodActivity.this, DetectorActivity.class);
            startActivity(ice_i);
        });


        Button button_drink = findViewById(R.id.button_drink);
        button_drink.setOnClickListener(v -> {
            Intent drink_i = new Intent(SelectFoodActivity.this, DetectorActivity.class);
            startActivity(drink_i);
        });



    }
}