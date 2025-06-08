package com.example.calorease;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CustomMealActivity extends AppCompatActivity {

    private EditText editName, editCalories, editProtein, editCarbs, editFat, editQuantity;
    private Button btnSave;

    private String mealCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_meal);

        mealCategory = getIntent().getStringExtra("category");
        if (mealCategory == null) mealCategory = "Bilinmeyen";

        editName = findViewById(R.id.edit_meal_name);
        editCalories = findViewById(R.id.edit_calories);
        editProtein = findViewById(R.id.edit_protein);
        editCarbs = findViewById(R.id.edit_carbs);
        editFat = findViewById(R.id.edit_fat);
        editQuantity = findViewById(R.id.edit_quantity);
        btnSave = findViewById(R.id.btn_save_custom_meal);

        btnSave.setOnClickListener(v -> saveMeal());
    }

    private void saveMeal() {
        String name = editName.getText().toString().trim();
        int calories = parseInt(editCalories);
        int protein = parseInt(editProtein);
        int carbs = parseInt(editCarbs);
        int fat = parseInt(editFat);
        int quantity = parseInt(editQuantity);

        if (name.isEmpty() || quantity <= 0 || calories <= 0) {
            Toast.makeText(this, "Lütfen tüm alanları doğru doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String instanceId = UUID.randomUUID().toString();

        DatabaseManager.getInstance().addCustomMealInstance(
                uid,
                instanceId,
                date,
                mealCategory,
                name,
                calories,
                protein,
                carbs,
                fat,
                quantity,
                new DatabaseManager.OnMealInstanceAddedCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(CustomMealActivity.this, "Yemek eklendi!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(CustomMealActivity.this, "Hata: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int parseInt(EditText editText) {
        try {
            return Integer.parseInt(editText.getText().toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
