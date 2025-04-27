package com.example.calorease;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMealActivity extends AppCompatActivity {

    private EditText editMealName, editCalories, editProtein, editCarbs, editFat, editDescription;
    private Button buttonAddMeal;

    private FirebaseDatabase database;
    private DatabaseReference mealsRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        // Firebase bağlantıları
        database = FirebaseDatabase.getInstance();
        mealsRef = database.getReference("Meals");
        auth = FirebaseAuth.getInstance();

        // View'ları bağla
        editMealName = findViewById(R.id.edit_text_meal_name);
        editCalories = findViewById(R.id.edit_text_calories);
        editProtein = findViewById(R.id.edit_text_protein);
        editCarbs = findViewById(R.id.edit_text_carbs);
        editFat = findViewById(R.id.edit_text_fat);
        editDescription = findViewById(R.id.edit_text_description);
        buttonAddMeal = findViewById(R.id.button_add_meal);

        // Butona tıklanınca yemek ekle
        buttonAddMeal.setOnClickListener(v -> addMeal());
    }

    private void addMeal() {
        String mealName = editMealName.getText().toString().trim();
        String caloriesText = editCalories.getText().toString().trim();
        String proteinText = editProtein.getText().toString().trim();
        String carbsText = editCarbs.getText().toString().trim();
        String fatText = editFat.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        // Zorunlu alan kontrolleri
        if (mealName.isEmpty() || caloriesText.isEmpty() || proteinText.isEmpty() || carbsText.isEmpty() || fatText.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm gerekli alanları doldurun.", Toast.LENGTH_SHORT).show();
            return;
        }

        int calories = Integer.parseInt(caloriesText);
        int protein = Integer.parseInt(proteinText);
        int carbs = Integer.parseInt(carbsText);
        int fat = Integer.parseInt(fatText);

        // Firebase'e ekleme
        String mealId = mealsRef.push().getKey();

        if (mealId != null) {
            DatabaseReference mealRef = mealsRef.child(mealId);

            mealRef.child("name").setValue(mealName);
            mealRef.child("calories").setValue(calories);
            mealRef.child("protein").setValue(protein);
            mealRef.child("carbs").setValue(carbs);
            mealRef.child("fat").setValue(fat);
            mealRef.child("description").setValue(description.isEmpty() ? " " : description); // boşsa boş string
            mealRef.child("createdBy").setValue(auth.getCurrentUser().getUid());

            Toast.makeText(this, mealName + " başarıyla eklendi!", Toast.LENGTH_SHORT).show();
            finish(); // Ekleme bitince sayfayı kapat
        } else {
            Toast.makeText(this, "Yemek eklenemedi! (ID oluşturulamadı)", Toast.LENGTH_SHORT).show();
        }
    }
}
