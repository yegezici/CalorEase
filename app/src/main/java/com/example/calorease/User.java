package com.example.calorease;

public class User {

    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private int height;
    private int currentWeight;
    private int targetWeight;
    private int dailyCalorieGoal;

    public User() {
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


    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getHeight() {
        return height;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public int getTargetWeight() {
        return targetWeight;
    }

    public int getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }


}
