package com.example.calorease;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity {

    private ImageButton btnMenu, btnProfile, btnAddMeal, btnAddWater, btnAddExercise, btnDietHistory;
    private ProgressBar progressCalories, progressWater;
    private Button btnRecommendation;
    private TextView tvCalories, tvProtein, tvCarbs, tvFat, textGreeting, textDailyTip, textCalorieRatio, textWaterLabel;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        progressCalories.setMax(dailyCalorieGoal);
        progressWater.setMax(DAILY_WATER_GOAL);


        loadUserName();
        List<String> dailyTips = Arrays.asList(
                "Günün ipucu: Yavaş yemek daha az kalori almanı sağlar 🥗",
                "Günün ipucu: Her gün 2-3 litre su içmeye çalış 💧",
                "Günün ipucu: Uyku düzeni, kilo kontrolü kadar önemlidir 🛌",
                "Günün ipucu: Şekersiz çay ve kahve kalorisizdir ☕",
                "Günün ipucu: Kahvaltıyı atlama, metabolizmanı hızlandırır 🍳",
                "Günün ipucu: Etiketleri kontrol et, gizli şekerlere dikkat! 🏷️",
                "Günün ipucu: Günlük 30 dakika yürüyüş hem bedene hem ruha iyi gelir 🚶",
                "Günün ipucu: Öğünlerini planlamak gereksiz atıştırmaları önler 📅",
                "Günün ipucu: Lifli gıdalar tokluk hissini artırır 🥦",
                "Günün ipucu: Abur cuburları ulaşamayacağın yere koy 🍫❌",
                "Günün ipucu: Kendini aç bırakma, dengeli beslen 🚫🍽️",
                "Günün ipucu: Yağsız yoğurt iyi bir protein kaynağıdır 🥣",
                "Günün ipucu: Akşam yemeğini uyumadan en az 3 saat önce bitir 🕒",
                "Günün ipucu: Günlük kalori hedefini aşmamaya dikkat et 🎯",
                "Günün ipucu: Evde egzersiz yapmak da çok işe yarar 🏋️",
                "Günün ipucu: Sağlıklı atıştırmalıklar hazırlayarak açlık krizlerini önle! 🍎",
                "Günün ipucu: Bol sebzeli yemekler hem doyurucu hem düşük kalorilidir 🥗",
                "Günün ipucu: Su içmek açlık hissini azaltabilir 💦",
                "Günün ipucu: Kendine ödül ver ama ölçüyü kaçırma 🎁",
                "Günün ipucu: Küçük tabaklar kullanmak porsiyon kontrolüne yardımcı olur 🍽️"
        );


        Random random = new Random();
        String selectedTip = dailyTips.get(random.nextInt(dailyTips.size()));
        textDailyTip.setText(selectedTip);


        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Ana sayfadasın
            } else if (itemId == R.id.nav_add_meal) {
                startActivity(new Intent(this, SelectMealCategory.class));
            } else if (itemId == R.id.nav_add_water) {
                startActivity(new Intent(this, AddWaterActivity.class));
            } else if (itemId == R.id.nav_recommendation) {
                startActivity(new Intent(this, ChatbotActivity.class));
            } else if (itemId == R.id.nav_stats) {
                startActivity(new Intent(this, StatsActivity.class));
            } else if (itemId == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LogIn.class));
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnAddMeal.setOnClickListener(v -> startActivity(new Intent(this, SelectMealCategory.class)));
        btnAddWater.setOnClickListener(v -> startActivity(new Intent(this, AddWaterActivity.class)));
        btnAddExercise.setOnClickListener(v -> Toast.makeText(this, "Egzersiz ekleme henüz aktif değil", Toast.LENGTH_SHORT).show());
        btnDietHistory.setOnClickListener(v -> startActivity(new Intent(this, DietHistoryActivity.class)));
        btnRecommendation.setOnClickListener(v -> startActivity(new Intent(this, ChatbotActivity.class)));

        applyScaleAnimation(btnAddMeal);
        applyScaleAnimation(btnAddWater);
        applyScaleAnimation(btnAddExercise);
        applyScaleAnimation(btnDietHistory);
        applyScaleAnimation(btnRecommendation);
        applyScaleAnimation(btnMenu);
        applyScaleAnimation(btnProfile);
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
                loadTodaySummary();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(HomeActivity.this, "Hedef kalorisi alınamadı.", Toast.LENGTH_SHORT).show();
                loadTodaySummary();
            }
        });
    }

    private void loadTodaySummary() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DatabaseManager.getInstance().fetchTodaySummary(userId, today, new DatabaseManager.MealStatsCallback() {
            @Override
            public void onSuccess(double calories, double carbs, double protein, double fat) {
                updateProgress(calories, protein, carbs, fat);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(HomeActivity.this, "Veri alınamadı: " + error, Toast.LENGTH_SHORT).show();
                updateProgress(0, 0, 0, 0);
            }
        });
    }

    private void updateProgress(double calories, double protein, double carbs, double fat) {
        animateProgressBar(progressCalories, (int) calories);
        textCalorieRatio.setText(calories + " / " + dailyCalorieGoal + " kcal");

        double percent = (dailyCalorieGoal > 0) ? (calories * 100f / dailyCalorieGoal) : 0f;

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
                    animateProgressBar(progressWater, total);
                    textWaterLabel.setText(total + " / " + DAILY_WATER_GOAL + " ml");
                })
                .addOnFailureListener(e -> {
                    textWaterLabel.setText("Su bilgisi alınamadı");
                });
    }

    private void animateProgressBar(ProgressBar progressBar, int toValue) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, toValue);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void applyScaleAnimation(final View view) {
        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.button_click_scale);
        view.setOnTouchListener((v, event) -> {
            v.startAnimation(scaleAnim);
            return false;
        });
    }
}
