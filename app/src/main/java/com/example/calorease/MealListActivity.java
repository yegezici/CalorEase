package com.example.calorease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MealListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private List<Meal> mealList;
    private TextView textCategoryTitle;
    private FloatingActionButton fabAddMeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        recyclerView = findViewById(R.id.recycler_view_meal_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        textCategoryTitle = findViewById(R.id.text_category_title);
        String category = getIntent().getStringExtra("category");
        textCategoryTitle.setText("Yemek Listesi - " + category);

        mealList = new ArrayList<>();
        mealAdapter = new MealAdapter(this, mealList, category);
        recyclerView.setAdapter(mealAdapter);

        fabAddMeal = findViewById(R.id.fab_add_meal);
        fabAddMeal.setOnClickListener(v -> {
            Intent intent = new Intent(MealListActivity.this, AddMealActivity.class);
            startActivity(intent);
        });

        loadMealsFromFirestore();
    }

    private void loadMealsFromFirestore() {
        DatabaseManager.getInstance().getAllMeals(new DatabaseManager.MealListCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> mealsData) {
                mealList.clear();
                for (Map<String, Object> mealMap : mealsData) {
                    String mealId = (String) mealMap.get("id");
                    String name = (String) mealMap.get("name");
                    double protein = 0.0;
                    double fat = 0.0;
                    double carb = 0.0;
                    double calories = 0.0;

                    try {
                        protein = Double.parseDouble((String) mealMap.get("protein"));
                    } catch (Exception e) {
                        e.printStackTrace(); // veya loglama yapabilirsin
                    }

                    try {
                        fat = Double.parseDouble((String) mealMap.get("fat"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        carb = Double.parseDouble((String) mealMap.get("carb"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        calories = Double.parseDouble((String) mealMap.get("calories"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    Meal meal = new Meal(mealId, name,
                            calories + " kcal",
                            carb + "g",
                            protein + "g",
                            fat + "g",
                            android.R.drawable.ic_menu_zoom);

                    mealList.add(meal);
                }
                mealAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MealListActivity.this, "Yemekler yüklenemedi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int toInt(Object value) {
        return (value instanceof Number) ? ((Number) value).intValue() : 0;
    }
}
