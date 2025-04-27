package com.example.calorease;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class DietHistoryActivity extends AppCompatActivity {

    private TextView textTodayDate;
    private RecyclerView recyclerView;
    private DietMealAdapter dietMealAdapter;
    private List<DietMeal> dietMealList;

    private DatabaseReference userMealsRef;
    private DatabaseReference mealsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_history);

        textTodayDate = findViewById(R.id.text_today_date);
        recyclerView = findViewById(R.id.recycler_view_diet_history);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dietMealList = new ArrayList<>();
        dietMealAdapter = new DietMealAdapter(this, dietMealList);
        recyclerView.setAdapter(dietMealAdapter);

        String today = getTodayFormatted();
        textTodayDate.setText("Tarih: " + today);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userMealsRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Meals").child(getTodayDatabaseFormat());
        mealsRef = FirebaseDatabase.getInstance().getReference("Meals");

        loadDietHistory();
    }

    private void loadDietHistory() {
        userMealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dietMealList.clear();
                for (DataSnapshot mealCategorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot mealSnapshot : mealCategorySnapshot.getChildren()) {
                        String mealId = mealSnapshot.child("mealId").getValue(String.class);
                        Integer quantityValue = mealSnapshot.child("quantity").getValue(Integer.class);
                        int quantity = quantityValue != null ? quantityValue : 0;

                        mealsRef.child(mealId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot mealDetailsSnapshot) {
                                String name = mealDetailsSnapshot.child("name").getValue(String.class);

                                Integer caloriesValue = mealDetailsSnapshot.child("calories").getValue(Integer.class);
                                int calories = caloriesValue != null ? caloriesValue : 0;

                                Integer proteinValue = mealDetailsSnapshot.child("protein").getValue(Integer.class);
                                int protein = proteinValue != null ? proteinValue : 0;

                                Integer carbsValue = mealDetailsSnapshot.child("carbs").getValue(Integer.class);
                                int carbs = carbsValue != null ? carbsValue : 0;

                                Integer fatValue = mealDetailsSnapshot.child("fat").getValue(Integer.class);
                                int fat = fatValue != null ? fatValue : 0;

                                DietMeal dietMeal = new DietMeal(name, quantity, calories, protein, carbs, fat);
                                dietMealList.add(dietMeal);
                                dietMealAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(DietHistoryActivity.this, "Yemek detayı yüklenemedi!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DietHistoryActivity.this, "Veri yüklenemedi!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getTodayFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("tr", "TR"));
        return sdf.format(new Date());
    }

    private String getTodayDatabaseFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
