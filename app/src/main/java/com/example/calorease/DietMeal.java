package com.example.calorease;

public class DietMeal {
    public String name;
    public int quantity;
    public double calories;
    public double protein;
    public double carbs;
    public double fat;

    public DietMeal() {
    }

    public DietMeal(String name, int quantity, double calories, double protein, double carbs, double fat) {
        this.name = name;
        this.quantity = quantity;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }
    public double getCaloriesPer100g() {
        return calories; // kalori değeri zaten 100g bazlı ise
    }

}
