package com.example.calorease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ImageButton btnMenu, btnProfile, btnAddMeal, btnAddWater, btnAddExercise, btnDietHistory;
    private ProgressBar progressCalories;
    private TextView tvCalories, tvProtein, tvCarbs, tvFat;

    private int totalCalories = 0;
    private int totalProtein = 0;
    private int totalCarbs = 0;
    private int totalFat = 0;

    private int dailyCalorieGoal = 2000;

    @Override
    protected void onResume() {
        super.onResume();
        loadTodayMealsAndUpdateProgress();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnMenu = findViewById(R.id.btn_menu);
        btnProfile = findViewById(R.id.btn_profile);
        btnAddMeal = findViewById(R.id.btn_add_meal);
        btnAddWater = findViewById(R.id.btn_add_water);
        btnAddExercise = findViewById(R.id.btn_add_exercise);
        btnDietHistory = findViewById(R.id.btn_diet_history);

        progressCalories = findViewById(R.id.progress_calories);

        tvCalories = findViewById(R.id.tv_calories);
        tvProtein = findViewById(R.id.tv_protein);
        tvCarbs = findViewById(R.id.tv_carbs);
        tvFat = findViewById(R.id.tv_fat);

        progressCalories.setMax(dailyCalorieGoal);

        btnMenu.setOnClickListener(v ->
                Toast.makeText(HomeActivity.this, "Menü açılıyor...", Toast.LENGTH_SHORT).show());

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnAddMeal.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SelectMealCategory.class);
            startActivity(intent);
        });

        btnAddWater.setOnClickListener(v ->
                Toast.makeText(HomeActivity.this, "Su ekleme henüz aktif değil", Toast.LENGTH_SHORT).show());

        btnAddExercise.setOnClickListener(v ->
                Toast.makeText(HomeActivity.this, "Egzersiz ekleme henüz aktif değil", Toast.LENGTH_SHORT).show());

        btnDietHistory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DietHistoryActivity.class);
            startActivity(intent);
        });


    }



    private void loadTodayMealsAndUpdateProgress() {
        String todayDate = getTodayDate();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference userMealsRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("Meals")
                .child(todayDate);

        userMealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalCalories = 0;
                totalProtein = 0;
                totalCarbs = 0;
                totalFat = 0;

                List<Pair<Task<DataSnapshot>, Integer>> mealTasks = new ArrayList<>();

                for (DataSnapshot mealTypeSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot mealSnapshot : mealTypeSnapshot.getChildren()) {
                        String mealId = mealSnapshot.child("mealId").getValue(String.class);
                        Long quantityLong = mealSnapshot.child("quantity").getValue(Long.class);
                        int quantity = (quantityLong != null) ? quantityLong.intValue() : 0;

                        if (mealId != null && quantity > 0) {
                            Task<DataSnapshot> task = FirebaseDatabase.getInstance()
                                    .getReference("Meals")
                                    .child(mealId)
                                    .get();
                            mealTasks.add(new Pair<>(task, quantity));
                        }
                    }
                }

                List<Task<?>> onlyTasks = new ArrayList<>();
                for (Pair<Task<DataSnapshot>, Integer> pair : mealTasks) {
                    onlyTasks.add(pair.first);
                }

                Tasks.whenAllComplete(onlyTasks).addOnCompleteListener(task -> {
                    for (Pair<Task<DataSnapshot>, Integer> pair : mealTasks) {
                        if (pair.first.isSuccessful()) {
                            DataSnapshot mealData = pair.first.getResult();
                            int quantity = pair.second;
                            if (mealData.exists()) {
                                Long baseCalories = mealData.child("calories").getValue(Long.class);
                                Long baseProtein = mealData.child("protein").getValue(Long.class);
                                Long baseCarbs = mealData.child("carbs").getValue(Long.class);
                                Long baseFat = mealData.child("fat").getValue(Long.class);

                                if (baseCalories != null) totalCalories += baseCalories * quantity / 100;
                                if (baseProtein != null) totalProtein += baseProtein * quantity / 100;
                                if (baseCarbs != null) totalCarbs += baseCarbs * quantity / 100;
                                if (baseFat != null) totalFat += baseFat * quantity / 100;
                            }
                        }
                    }
                    updateProgress(totalCalories, totalProtein, totalCarbs, totalFat);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Veri yüklenemedi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProgress(int calories, int protein, int carbs, int fat) {
        progressCalories.setProgress(calories);

        tvCalories.setText("Günlük Kalori: " + calories + " kcal");
        tvProtein.setText("Protein: " + protein + " g");
        tvCarbs.setText("Karbonhidrat: " + carbs + " g");
        tvFat.setText("Yağ: " + fat + " g");
    }

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
