package com.example.calorease;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private Context context;
    private List<Meal> mealList;
    private String selectedCategory; // Öğün bilgisi (Kahvaltı, Öğle vs.)

    public MealAdapter(Context context, List<Meal> mealList, String selectedCategory) {
        this.context = context;
        this.mealList = mealList;
        this.selectedCategory = selectedCategory;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meal_item, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.mealName.setText(meal.getName());
        holder.mealCalories.setText(meal.getCalories());
        holder.mealCarbs.setText("Karbonhidrat: " + meal.getCarbs());
        holder.mealProtein.setText("Protein: " + meal.getProtein());
        holder.mealFat.setText("Yağ: " + meal.getFat());
        holder.mealIcon.setImageResource(meal.getImageResource());

        // Yemek kartına tıklanınca MealDetailActivity'ye geç
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealDetailActivity.class);
            intent.putExtra("mealId", meal.getName()); // Şimdilik mealId yok, mealName kullanıyoruz
            intent.putExtra("mealName", meal.getName());
            intent.putExtra("mealCategory", selectedCategory); // Öğün bilgisi
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {

        TextView mealName, mealCalories, mealCarbs, mealProtein, mealFat;
        ImageView mealIcon;
        ImageButton addButton;

        public MealViewHolder(View itemView) {
            super(itemView);
            mealName = itemView.findViewById(R.id.meal_name);
            mealCalories = itemView.findViewById(R.id.meal_calories);
            mealCarbs = itemView.findViewById(R.id.meal_carbs);
            mealProtein = itemView.findViewById(R.id.meal_protein);
            mealFat = itemView.findViewById(R.id.meal_fat);
            mealIcon = itemView.findViewById(R.id.meal_icon);
            addButton = itemView.findViewById(R.id.btn_add_meal); // Şu anda kullanılmıyor ama dursun.
        }
    }
}
