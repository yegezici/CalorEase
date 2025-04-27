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
                openMealList("Kahvaltı");
            }
        });

        // Öğle Yemeği
        btnLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMealList("Öğle Yemeği");
            }
        });

        // Akşam Yemeği
        btnDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMealList("Akşam Yemeği");
            }
        });

        // Atıştırmalık
        btnSnack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMealList("Atıştırmalık");
            }
        });
    }

    private void openMealList(String category) {
        Intent intent = new Intent(SelectMealCategory.this, MealListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
