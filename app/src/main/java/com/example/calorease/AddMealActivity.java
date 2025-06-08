package com.example.calorease;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AddMealActivity extends AppCompatActivity {

    private EditText editMealName, editCalories, editProtein, editCarbs, editFat, editDescription;
    private Button buttonAddMeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        // View'ları bağla
        editMealName = findViewById(R.id.edit_text_meal_name);
        editCalories = findViewById(R.id.edit_text_calories);
        editProtein = findViewById(R.id.edit_text_protein);
        editCarbs = findViewById(R.id.edit_text_carbs);
        editFat = findViewById(R.id.edit_text_fat);
        editDescription = findViewById(R.id.edit_text_description);
        buttonAddMeal = findViewById(R.id.button_add_meal);

        buttonAddMeal.setOnClickListener(v -> addMeal());
    }

    private void addMeal() {
        String mealName = editMealName.getText().toString().trim();
        String caloriesText = editCalories.getText().toString().trim();
        String proteinText = editProtein.getText().toString().trim();
        String carbsText = editCarbs.getText().toString().trim();
        String fatText = editFat.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (mealName.isEmpty() || caloriesText.isEmpty() || proteinText.isEmpty()
                || carbsText.isEmpty() || fatText.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm gerekli alanları doldurun.", Toast.LENGTH_SHORT).show();
            return;
        }

        int calories = Integer.parseInt(caloriesText);
        int protein = Integer.parseInt(proteinText);
        int carbs = Integer.parseInt(carbsText);
        int fat = Integer.parseInt(fatText);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseManager.getInstance().addMeal(mealName, calories, protein, carbs, fat, description, userId,
                new DatabaseManager.OnMealAddedCallback() {
                    @Override
                    public void onSuccess(String mealId) {
                        Toast.makeText(AddMealActivity.this, mealName + " başarıyla eklendi!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(AddMealActivity.this, "Yemek eklenemedi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
