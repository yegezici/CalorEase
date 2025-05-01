package com.example.calorease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SelectMealCategory extends AppCompatActivity {

    private Button btnBreakfast, btnLunch, btnDinner, btnSnack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_meal_category);

        // Butonları bağla
        btnBreakfast = findViewById(R.id.btn_breakfast);
        btnLunch = findViewById(R.id.btn_lunch);
        btnDinner = findViewById(R.id.btn_dinner);
        btnSnack = findViewById(R.id.btn_snack);

        // Kahvaltı
        btnBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMealOption("Kahvaltı");
            }
        });

        // Öğle Yemeği
        btnLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMealOption("Öğle Yemeği");
            }
        });

        // Akşam Yemeği
        btnDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMealOption("Akşam Yemeği");
            }
        });

        // Atıştırmalık
        btnSnack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMealOption("Atıştırmalık");
            }
        });
    }

    private void openMealOption(String category) {
        Intent intent = new Intent(SelectMealCategory.this, SelectMealOptionActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

}
