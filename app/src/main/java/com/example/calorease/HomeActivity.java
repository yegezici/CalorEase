package com.example.calorease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private ImageButton btnMenu, btnProfile, btnAddMeal, btnAddWater, btnAddExercise, btnDietHistory;
    private ProgressBar progressCalories;
    private Button btnRecommendation;
    private TextView tvCalories, tvProtein, tvCarbs, tvFat, textGreeting, textDailyTip, textCalorieRatio;

    private int totalCalories = 0;
    private int totalProtein = 0;
    private int totalCarbs = 0;
    private int totalFat = 0;

    private int dailyCalorieGoal = 2000;

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserCalorieGoalAndLoadMeals();
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
        textDailyTip = findViewById(R.id.text_daily_tip);
        textDailyTip.setText("Günün ipucu: Yavaş yemek daha az kalori almanı sağlar 🥗");
        btnRecommendation = findViewById(R.id.btn_recommendation);
        textCalorieRatio = findViewById(R.id.text_calorie_ratio);

        progressCalories = findViewById(R.id.progress_calories);
        tvCalories = findViewById(R.id.tv_calories);
        tvProtein = findViewById(R.id.tv_protein);
        tvCarbs = findViewById(R.id.tv_carbs);
        tvFat = findViewById(R.id.tv_fat);
        textGreeting = findViewById(R.id.text_greeting);

        loadUserName();
        progressCalories.setMax(dailyCalorieGoal);

        btnMenu.setOnClickListener(v -> Toast.makeText(this, "Menü açılıyor...", Toast.LENGTH_SHORT).show());
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnAddMeal.setOnClickListener(v -> startActivity(new Intent(this, SelectMealCategory.class)));
        btnAddWater.setOnClickListener(v -> Toast.makeText(this, "Su ekleme henüz aktif değil", Toast.LENGTH_SHORT).show());
        btnAddExercise.setOnClickListener(v -> Toast.makeText(this, "Egzersiz ekleme henüz aktif değil", Toast.LENGTH_SHORT).show());
        btnDietHistory.setOnClickListener(v -> startActivity(new Intent(this, DietHistoryActivity.class)));
        btnRecommendation.setOnClickListener(v -> startActivity(new Intent(this, ChatbotActivity.class)));
    }

    private void loadUserName() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseManager.getInstance().fetchUser(userId, new DatabaseManager.UserCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                String name = (String) userData.get("firstName");
                textGreeting.setText("Merhaba, " + (name != null ? name : "") + "!");
            }

            @Override
            public void onFailure(String error) {
                textGreeting.setText("Merhaba!");
            }
        });
    }

    private void fetchUserCalorieGoalAndLoadMeals() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseManager.getInstance().fetchUser(userId, new DatabaseManager.UserCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                Object goalObj = userData.get("dailyCalorieGoal");
                if (goalObj instanceof Number) {
                    dailyCalorieGoal = ((Number) goalObj).intValue();
                    progressCalories.setMax(dailyCalorieGoal);
                }
                loadTodayMealsAndUpdateProgress();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(HomeActivity.this, "Hedef kalorisi alınamadı.", Toast.LENGTH_SHORT).show();
                loadTodayMealsAndUpdateProgress();
            }
        });
    }

    private void loadTodayMealsAndUpdateProgress() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String today = getTodayDate();

        DatabaseManager.getInstance().fetchMealInstancesForToday(userId, today, new DatabaseManager.MealInstanceCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> mealInstances) {
                totalCalories = 0;
                totalProtein = 0;
                totalCarbs = 0;
                totalFat = 0;

                for (Map<String, Object> instance : mealInstances) {
                    List<Map<String, Object>> meals = (List<Map<String, Object>>) instance.get("meals");
                    if (meals != null) {
                        for (Map<String, Object> mealEntry : meals) {
                            String mealID = (String) mealEntry.get("mealID");
                            Object qtyObj = mealEntry.get("quantity");
                            int quantity = (qtyObj instanceof Number) ? ((Number) qtyObj).intValue() : 0;

                            if (mealID != null && quantity > 0) {
                                DatabaseManager.getInstance().fetchMealById(mealID, new DatabaseManager.MealDetailCallback() {
                                    @Override
                                    public void onSuccess(Map<String, Object> mealData) {
                                        int cal = toInt(mealData.get("calories"));
                                        int pro = toInt(mealData.get("protein"));
                                        int carb = toInt(mealData.get("carb"));
                                        int fat = toInt(mealData.get("fat"));

                                        totalCalories += cal * quantity / 100;
                                        totalProtein += pro * quantity / 100;
                                        totalCarbs += carb * quantity / 100;
                                        totalFat += fat * quantity / 100;

                                        updateProgress(totalCalories, totalProtein, totalCarbs, totalFat);
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        // isteğe bağlı: hata loglanabilir
                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(HomeActivity.this, "Veri alınamadı: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int toInt(Object obj) {
        return (obj instanceof Number) ? ((Number) obj).intValue() : 0;
    }

    private void updateProgress(int calories, int protein, int carbs, int fat) {
        progressCalories.setProgress(calories);
        textCalorieRatio.setText(calories + " / " + dailyCalorieGoal + " kcal");

        float percent = (dailyCalorieGoal > 0) ? (calories * 100f / dailyCalorieGoal) : 0f;

        if (percent <= 25) {
            progressCalories.setProgressDrawable(getDrawable(R.drawable.progress_danger));
        } else if (percent <= 75) {
            progressCalories.setProgressDrawable(getDrawable(R.drawable.progress_warning));
        } else {
            progressCalories.setProgressDrawable(getDrawable(R.drawable.progress_success));
        }

        tvCalories.setText("Günlük Kalori: " + calories + " kcal");
        tvProtein.setText("Protein: " + protein + " g");
        tvCarbs.setText("Karbonhidrat: " + carbs + " g");
        tvFat.setText("Yağ: " + fat + " g");
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}
