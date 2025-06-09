package com.example.calorease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SelectMealCategory extends AppCompatActivity {

    private Button btnBreakfast, btnLunch, btnDinner, btnSnack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_meal_category);

        btnBreakfast = findViewById(R.id.btn_breakfast);
        btnLunch = findViewById(R.id.btn_lunch);
        btnDinner = findViewById(R.id.btn_dinner);
        btnSnack = findViewById(R.id.btn_snack);

        setupAnimatedClick(btnBreakfast, "Kahvaltı");
        setupAnimatedClick(btnLunch, "Öğle Yemeği");
        setupAnimatedClick(btnDinner, "Akşam Yemeği");
        setupAnimatedClick(btnSnack, "Atıştırmalık");
    }

    private void setupAnimatedClick(Button button, String category) {
        button.setOnClickListener(v -> {
            animateButton(v);
            v.postDelayed(() -> {
                Intent intent = new Intent(SelectMealCategory.this, SelectMealOptionActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }, 150);
        });
    }

    private void animateButton(View view) {
        ScaleAnimation scaleDown = new ScaleAnimation(
                1f, 0.94f, 1f, 0.94f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleDown.setDuration(70);
        scaleDown.setFillAfter(true);

        ScaleAnimation scaleUp = new ScaleAnimation(
                0.94f, 1f, 0.94f, 1f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setDuration(70);
        scaleUp.setStartOffset(70);
        scaleUp.setFillAfter(true);

        view.startAnimation(scaleDown);
        view.startAnimation(scaleUp);
    }
}
