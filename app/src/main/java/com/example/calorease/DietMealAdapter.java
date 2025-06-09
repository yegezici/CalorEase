package com.example.calorease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DietMealAdapter extends RecyclerView.Adapter<DietMealAdapter.DietMealViewHolder> {

    private Context context;
    private List<DietMeal> dietMealList;

    public DietMealAdapter(Context context, List<DietMeal> dietMealList) {
        this.context = context;
        this.dietMealList = dietMealList;
    }

    @NonNull
    @Override
    public DietMealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_diet_meal, parent, false);
        return new DietMealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DietMealViewHolder holder, int position) {
        DietMeal meal = dietMealList.get(position);

        holder.textMealName.setText(meal.name != null ? meal.name : "Yemek Adı Yok");

        if (meal.calories > 200) {
            holder.textHighCalorieWarning.setVisibility(View.VISIBLE);
        } else {
            holder.textHighCalorieWarning.setVisibility(View.GONE);
        }

        holder.textMealDetails.setText(
                meal.quantity + "g - " +
                        meal.calories + " kcal - " +
                        meal.protein + "g Protein - " +
                        meal.carbs + "g Karbonhidrat - " +
                        meal.fat + "g Yağ"
        );
    }

    @Override
    public int getItemCount() {
        return dietMealList.size();
    }

    public static class DietMealViewHolder extends RecyclerView.ViewHolder {

        TextView textMealName, textMealDetails, textHighCalorieWarning;

        public DietMealViewHolder(@NonNull View itemView) {
            super(itemView);
            textMealName = itemView.findViewById(R.id.text_meal_name);
            textMealDetails = itemView.findViewById(R.id.text_meal_details);
            textHighCalorieWarning = itemView.findViewById(R.id.text_high_calorie_warning);
        }
    }
}
