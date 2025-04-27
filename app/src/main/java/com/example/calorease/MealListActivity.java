package com.example.calorease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MealListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private List<Meal> mealList;
    private TextView textCategoryTitle;
    private DatabaseReference mealsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        recyclerView = findViewById(R.id.recycler_view_meal_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        textCategoryTitle = findViewById(R.id.text_category_title);

        // SelectMealCategory'den gelen öğün bilgisi (başlığı değiştirmeye devam edelim)
        String category = getIntent().getStringExtra("category");
        textCategoryTitle.setText("Yemek Listesi - " + category);

        mealList = new ArrayList<>();
        mealAdapter = new MealAdapter(this, mealList, category);
        recyclerView.setAdapter(mealAdapter);

        mealsRef = FirebaseDatabase.getInstance().getReference("Meals");

        loadMealsFromFirebase();

        Button btnAddNewMeal = findViewById(R.id.btn_add_new_meal);
        btnAddNewMeal.setOnClickListener(v -> {
            Intent intent = new Intent(MealListActivity.this, AddMealActivity.class);
            startActivity(intent);
        });
    }

    private void loadMealsFromFirebase() {
        mealsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mealList.clear();
                for (DataSnapshot mealSnapshot : snapshot.getChildren()) {
                    String name = mealSnapshot.child("name").getValue(String.class);
                    int calories = mealSnapshot.child("calories").getValue(Integer.class);
                    int protein = mealSnapshot.child("protein").getValue(Integer.class);
                    int carbs = mealSnapshot.child("carbs").getValue(Integer.class);
                    int fat = mealSnapshot.child("fat").getValue(Integer.class);

                    Meal meal = new Meal(name, calories + " kcal", carbs + "g", protein + "g", fat + "g", android.R.drawable.ic_menu_zoom);

                    mealList.add(meal);

                    // Hemen Log ekleyelim
                    System.out.println("Firebase'den gelen yemek: " + name);
                }
                mealAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MealListActivity.this, "Yemekler yüklenemedi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
