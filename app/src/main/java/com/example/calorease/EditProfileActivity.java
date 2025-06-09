package com.example.calorease;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private NumberPicker pickerHeight, pickerWeight;
    private Slider sliderGoal;
    private TextView textGoalValue;
    private MaterialButton buttonSave;

    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        pickerHeight = findViewById(R.id.picker_height);
        pickerWeight = findViewById(R.id.picker_weight);
        sliderGoal = findViewById(R.id.slider_goal);
        textGoalValue = findViewById(R.id.text_goal_value);
        buttonSave = findViewById(R.id.button_save_profile);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        pickerHeight.setMinValue(100);
        pickerHeight.setMaxValue(250);
        pickerWeight.setMinValue(30);
        pickerWeight.setMaxValue(200);
        sliderGoal.setValueFrom(1000f);
        sliderGoal.setValueTo(4000f);
        sliderGoal.setStepSize(50f);

        sliderGoal.addOnChangeListener((slider, value, fromUser) -> {
            textGoalValue.setText((int) value + " kcal");
        });

        DatabaseManager.getInstance().fetchUser(userId, new DatabaseManager.UserCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                if (userData.containsKey("height"))
                    pickerHeight.setValue(((Number) userData.get("height")).intValue());

                if (userData.containsKey("currentWeight"))  // düzeltildi
                    pickerWeight.setValue(((Number) userData.get("currentWeight")).intValue());

                if (userData.containsKey("dailyCalorieGoal")) {
                    int goal = ((Number) userData.get("dailyCalorieGoal")).intValue();
                    sliderGoal.setValue(goal);
                    textGoalValue.setText(goal + " kcal");
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(EditProfileActivity.this, "Veri alınamadı: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        buttonSave.setOnClickListener(v -> {
            int height = pickerHeight.getValue();
            int weight = pickerWeight.getValue();
            int goal = (int) sliderGoal.getValue();

            Map<String, Object> updates = new HashMap<>();
            updates.put("height", height);
            updates.put("currentWeight", weight); // düzeltildi
            updates.put("dailyCalorieGoal", goal);

            firestore.collection("Users").document(userId)
                    .set(updates, SetOptions.merge())
                    .addOnSuccessListener(unused -> {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Profil başarıyla güncellendi", Snackbar.LENGTH_SHORT);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(Color.parseColor("#1B5E20")); // koyu yeşil
                        snackbar.show();

                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                            finish();
                        }, 1200);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
