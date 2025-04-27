package com.example.calorease;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MealListFragment extends Fragment {

    private static final String ARG_MEAL_TYPE = "meal_type";
    private static final String ARG_DATE = "date";

    private String mealType;
    private String selectedDate;

    private RecyclerView recyclerView;
    private DietMealAdapter dietMealAdapter;
    private List<DietMeal> dietMealList;

    public MealListFragment() {}

    public static MealListFragment newInstance(String mealType, String date) {
        MealListFragment fragment = new MealListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEAL_TYPE, mealType);
        args.putString(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mealType = getArguments().getString(ARG_MEAL_TYPE);
            selectedDate = getArguments().getString(ARG_DATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_meal_list_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dietMealList = new ArrayList<>();
        dietMealAdapter = new DietMealAdapter(getContext(), dietMealList);
        recyclerView.setAdapter(dietMealAdapter);

        loadMeals();

        return view;
    }

    private void loadMeals() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userMealsRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("Meals")
                .child(selectedDate)
                .child(mealType);

        DatabaseReference mealsRef = FirebaseDatabase.getInstance().getReference("Meals");

        userMealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dietMealList.clear();
                for (DataSnapshot mealSnapshot : snapshot.getChildren()) {
                    String mealId = mealSnapshot.child("mealId").getValue(String.class);
                    Integer quantityValue = mealSnapshot.child("quantity").getValue(Integer.class);
                    int quantity = quantityValue != null ? quantityValue : 0;

                    if (mealId != null) {
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
                                Toast.makeText(getContext(), "Yemek detayı yüklenemedi!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Veri yüklenemedi!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
