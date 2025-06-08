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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        DatabaseManager.getInstance().getMealInstances(userId, selectedDate, mealType, new DatabaseManager.MealInstanceListCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> mealEntries) {
                dietMealList.clear();

                for (Map<String, Object> entry : mealEntries) {
                    String mealId = (String) entry.get("mealId");
                    Long quantityLong = (Long) entry.get("quantity");
                    int quantity = quantityLong != null ? quantityLong.intValue() : 0;

                    if (mealId != null) {
                        // Hazır yemek
                        DatabaseManager.getInstance().fetchMealById(mealId, new DatabaseManager.MealDetailCallback() {
                            @Override
                            public void onSuccess(Map<String, Object> mealData) {
                                String name = (String) mealData.get("name");
                                String calories = (String) (mealData.get("calories"));
                                String protein = (String) (mealData.get("protein"));
                                String carbs = (String) (mealData.get("carb"));
                                String fat = (String) (mealData.get("fat"));

                                dietMealList.add(new DietMeal(name, quantity, Double.parseDouble(calories), Double.parseDouble(protein),
                                        Double.parseDouble(carbs), Double.parseDouble(fat)));
                                dietMealAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(getContext(), "Yemek detayı yüklenemedi", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Özel yemek
                        String name = (String) entry.get("name");
                        int calories = toInt(entry.get("calories"));
                        int protein = toInt(entry.get("protein"));
                        int carbs = toInt(entry.get("carb"));
                        int fat = toInt(entry.get("fat"));

                        dietMealList.add(new DietMeal(name, quantity, calories, protein, carbs, fat));
                        dietMealAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Veri yüklenemedi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            return 0;
        }
    }
}
