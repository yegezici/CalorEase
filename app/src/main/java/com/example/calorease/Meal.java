package com.example.calorease;

public class Meal {
    private String name;
    private String calories;  // Kalori bilgisi
    private String carbs;     // Karbonhidrat miktarı
    private String protein;   // Protein miktarı
    private String fat;       // Yağ miktarı
    private int imageResource;  // Yemek görseli

    // Constructor
    public Meal(String name, String calories, String carbs, String protein, String fat, int imageResource) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.imageResource = imageResource;
    }

    // Getter ve Setter'lar
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
