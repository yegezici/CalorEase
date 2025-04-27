package com.example.calorease;

public class User {
    public String userId;
    public String email;
    public String firstName;
    public String lastName;
    public int height; // cm
    public int currentWeight; // kg
    public int targetWeight; // kg
    public int dailyCalorieGoal; // kalori

    public User() {
        // Boş constructor Firebase için lazım
    }

    public User(String userId, String email, String firstName, String lastName, int height, int currentWeight, int targetWeight, int dailyCalorieGoal) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.height = height;
        this.currentWeight = currentWeight;
        this.targetWeight = targetWeight;
        this.dailyCalorieGoal = dailyCalorieGoal;
    }
}
