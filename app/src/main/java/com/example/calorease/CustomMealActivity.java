package com.example.calorease;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class CustomMealActivity extends AppCompatActivity {

    private EditText editName, editCalories, editProtein, editCarbs, editFat, editQuantity;
    private Button btnSave;

    private String mealCategory; // öğün türü (Kahvaltı, Öğle Yemeği vb.)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_meal);

        // Kategoriyi SelectMealOptionActivity'den al
        mealCategory = getIntent().getStringExtra("category");
        if (mealCategory == null) mealCategory = "Bilinmeyen";

        // View'ları bağla
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
        String mealId = UUID.randomUUID().toString();

        // Kaydedilecek veriler
        HashMap<String, Object> mealData = new HashMap<>();
        mealData.put("name", name);
        mealData.put("calories", calories);
        mealData.put("protein", protein);
        mealData.put("carbs", carbs);
        mealData.put("fat", fat);
        mealData.put("quantity", quantity); // gram cinsinden

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("Meals")
                .child(date)
                .child(mealCategory)
                .child(mealId);

        ref.setValue(mealData).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Yemek eklendi!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
