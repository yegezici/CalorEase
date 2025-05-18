package com.example.calorease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SelectMealOptionActivity extends AppCompatActivity {

    private Button btnSelectFromList, btnAddCustomMeal;

    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_meal_option);

        category = getIntent().getStringExtra("category");

        btnSelectFromList = findViewById(R.id.btn_select_from_list);
        btnAddCustomMeal = findViewById(R.id.btn_add_custom_meal);

        btnSelectFromList.setOnClickListener(v -> {
            Intent intent = new Intent(this, MealListActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        });

        btnAddCustomMeal.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomMealActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        });
    }

}
