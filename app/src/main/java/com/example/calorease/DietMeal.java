package com.example.calorease;

public class DietMeal {
    public String name;
    public int quantity;
    public int calories;
    public int protein;
    public int carbs;
    public int fat;

    public DietMeal() {
    }

    public DietMeal(String name, int quantity, int calories, int protein, int carbs, int fat) {
        this.name = name;
        this.quantity = quantity;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }
}
