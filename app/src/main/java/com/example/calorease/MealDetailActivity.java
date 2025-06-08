package com.example.calorease;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MealDetailActivity extends AppCompatActivity {

    private TextView mealNameTextView;
    private EditText quantityEditText;
    private Button addMealButton;

    private String mealId;
    private String mealName;
    private String mealCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);

        mealNameTextView = findViewById(R.id.text_meal_name);
        quantityEditText = findViewById(R.id.edit_text_quantity);
        addMealButton = findViewById(R.id.button_add_meal);

        mealId = getIntent().getStringExtra("mealId");
        mealName = getIntent().getStringExtra("mealName");
        mealCategory = getIntent().getStringExtra("mealCategory");

        mealNameTextView.setText(mealName);

        addMealButton.setOnClickListener(v -> {
            String quantityText = quantityEditText.getText().toString().trim();
            if (quantityText.isEmpty()) {
                Toast.makeText(this, "Lütfen gram miktarını giriniz.", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            DatabaseManager.getInstance().addMealInstance(mealId, quantity, mealCategory, new DatabaseManager.DatabaseCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MealDetailActivity.this, mealName + " eklendi!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(MealDetailActivity.this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
