package com.example.calorease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private ImageButton btnMenu, btnProfile, btnAddMeal, btnAddWater, btnAddExercise, btnDietHistory;
    private ProgressBar progressCalories, progressWater;
    private Button btnRecommendation;
    private TextView tvCalories, tvProtein, tvCarbs, tvFat, textGreeting, textDailyTip, textCalorieRatio, textWaterLabel;

    private int totalCalories = 0;
    private int totalProtein = 0;
    private int totalCarbs = 0;
    private int totalFat = 0;
    private final int DAILY_WATER_GOAL = 2000;

    private int dailyCalorieGoal = 2000;

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserCalorieGoalAndLoadMeals();
        loadDailyWaterIntake();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LogIn.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        btnMenu = findViewById(R.id.btn_menu);
        btnProfile = findViewById(R.id.btn_profile);
        btnAddMeal = findViewById(R.id.btn_add_meal);
        btnAddWater = findViewById(R.id.btn_add_water);
        btnAddExercise = findViewById(R.id.btn_add_exercise);
        btnDietHistory = findViewById(R.id.btn_diet_history);
        textDailyTip = findViewById(R.id.text_daily_tip);
        btnRecommendation = findViewById(R.id.btn_recommendation);
        textCalorieRatio = findViewById(R.id.text_calorie_ratio);

        progressCalories = findViewById(R.id.progress_calories);
        progressWater = findViewById(R.id.progress_water);
        tvCalories = findViewById(R.id.tv_calories);
        tvProtein = findViewById(R.id.tv_protein);
        tvCarbs = findViewById(R.id.tv_carbs);
        tvFat = findViewById(R.id.tv_fat);
        textGreeting = findViewById(R.id.text_greeting);
        textWaterLabel = findViewById(R.id.text_water_label);

        progressCalories.setMax(dailyCalorieGoal);
        progressWater.setMax(DAILY_WATER_GOAL);

        loadUserName();
        textDailyTip.setText("Günün ipucu: Yavaş yemek daha az kalori almanı sağlar 🥗");

        btnMenu.setOnClickListener(v -> Toast.makeText(this, "Menü açılıyor...", Toast.LENGTH_SHORT).show());
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnAddMeal.setOnClickListener(v -> startActivity(new Intent(this, SelectMealCategory.class)));
        btnAddWater.setOnClickListener(v -> startActivity(new Intent(this, AddWaterActivity.class)));
        btnAddExercise.setOnClickListener(v -> Toast.makeText(this, "Egzersiz ekleme henüz aktif değil", Toast.LENGTH_SHORT).show());
        btnDietHistory.setOnClickListener(v -> startActivity(new Intent(this, DietHistoryActivity.class)));
        btnRecommendation.setOnClickListener(v -> startActivity(new Intent(this, ChatbotActivity.class)));
    }

    private void loadUserName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
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
                            Object qtyObj = mealEntry.get("quantity");
                            int quantity = (qtyObj instanceof Number) ? ((Number) qtyObj).intValue() : 0;

                            if (mealEntry.containsKey("mealID")) {
                                String mealID = (String) mealEntry.get("mealID");
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
                                        public void onFailure(String error) {}
                                    });
                                }
                            } else {
                                int cal = toInt(mealEntry.get("calories"));
                                int pro = toInt(mealEntry.get("protein"));
                                int carb = toInt(mealEntry.get("carb"));
                                int fat = toInt(mealEntry.get("fat"));

                                totalCalories += cal * quantity / 100;
                                totalProtein += pro * quantity / 100;
                                totalCarbs += carb * quantity / 100;
                                totalFat += fat * quantity / 100;

                                updateProgress(totalCalories, totalProtein, totalCarbs, totalFat);
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

    private void loadDailyWaterIntake() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(userId)
                .collection("WaterIntake")
                .document(today)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    int total = documentSnapshot.contains("amount") ? documentSnapshot.getLong("amount").intValue() : 0;
                    progressWater.setProgress(total);
                    textWaterLabel.setText(total + " / " + DAILY_WATER_GOAL + " ml");
                })
                .addOnFailureListener(e -> {
                    textWaterLabel.setText("Su bilgisi alınamadı");
                });
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}
