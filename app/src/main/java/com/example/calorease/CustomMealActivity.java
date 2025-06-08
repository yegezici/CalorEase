package com.example.calorease;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomMealActivity extends AppCompatActivity {

    private EditText editName, editCalories, editProtein, editCarbs, editFat, editQuantity;
    private Button btnSave;
    private CheckBox checkUnknownCalories;

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
        checkUnknownCalories = findViewById(R.id.checkbox_unknown_calories);
        btnSave = findViewById(R.id.btn_save_custom_meal);

        checkUnknownCalories.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editCalories.setEnabled(!isChecked);
            if (isChecked) {
                editCalories.setText("");
                updateCaloriesFromMacros();  // otomatik hesaplama başlat
            }
        });

        // TextWatcher'ları makro değerler için ekle
        TextWatcher macroWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkUnknownCalories.isChecked()) {
                    updateCaloriesFromMacros();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        editProtein.addTextChangedListener(macroWatcher);
        editCarbs.addTextChangedListener(macroWatcher);
        editFat.addTextChangedListener(macroWatcher);

        btnSave.setOnClickListener(v -> saveMeal());
    }

    private void updateCaloriesFromMacros() {
        int protein = parseInt(editProtein);
        int carbs = parseInt(editCarbs);
        int fat = parseInt(editFat);
        int calculatedCalories = (4 * protein) + (4 * carbs) + (9 * fat);
        editCalories.setText(String.valueOf(calculatedCalories));
    }

    private void saveMeal() {
        String name = editName.getText().toString().trim();
        int calories = parseInt(editCalories);
        int protein = parseInt(editProtein);
        int carbs = parseInt(editCarbs);
        int fat = parseInt(editFat);
        int quantity = parseInt(editQuantity);

        if (name.isEmpty() || quantity <= 0 || (!checkUnknownCalories.isChecked() && calories <= 0)) {
            Toast.makeText(this, "Lütfen tüm alanları doğru doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DatabaseManager.getInstance().addCustomMealInstance(
                uid, date, mealCategory, name, calories, protein, carbs, fat, quantity,
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
