package com.example.calorease;

public class Meal {

    private String mealId; // YENİ EKLENDİ
    private String name;
    private String calories;
    private String carbs;
    private String protein;
    private String fat;
    private int imageResource;

    public Meal() {
        // Boş constructor
    }

    public Meal(String mealId, String name, String calories, String carbs, String protein, String fat, int imageResource) {
        this.mealId = mealId;
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.imageResource = imageResource;
    }

    public String getMealId() {
        return mealId;
    }

    public String getName() {
        return name;
    }

    public String getCalories() {
        return calories;
    }

    public String getCarbs() {
        return carbs;
    }

    public String getProtein() {
        return protein;
    }

    public String getFat() {
        return fat;
    }

    public int getImageResource() {
        return imageResource;
    }
}
