package com.example.calorease;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class DatabaseManager {

    private static DatabaseManager instance;
    private final FirebaseFirestore firestore;

    private DatabaseManager() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void saveUser(User user, OnUserSavedCallback callback) {
        firestore.collection("Users")
                .document(user.getUserId())
                .set(user, SetOptions.merge())
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface OnUserSavedCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public void addMeal(String name, int calories, int protein, int carb, int fat,
                        String description, String createdBy, OnMealAddedCallback callback) {

        Map<String, Object> mealData = new HashMap<>();
        mealData.put("name", name);
        mealData.put("calories", calories);
        mealData.put("protein", protein);
        mealData.put("carb", carb);
        mealData.put("fat", fat);
        mealData.put("description", description.isEmpty() ? " " : description);
        mealData.put("createdBy", createdBy);

        firestore.collection("Meal")
                .add(mealData)
                .addOnSuccessListener(documentReference -> callback.onSuccess(documentReference.getId()))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface OnMealAddedCallback {
        void onSuccess(String mealId);
        void onFailure(String errorMessage);
    }

    public void addCustomMealInstance(String userId, String instanceId, String date, String mealType,
                                      String name, int calories, int protein, int carbs, int fat, int quantity,
                                      OnMealInstanceAddedCallback callback) {

        Map<String, Object> mealInstance = new HashMap<>();
        mealInstance.put("name", name);
        mealInstance.put("calories", calories);
        mealInstance.put("protein", protein);
        mealInstance.put("carb", carbs);
        mealInstance.put("fat", fat);
        mealInstance.put("quantity", quantity); // gram
        mealInstance.put("mealType", mealType);
        mealInstance.put("date", date);
        mealInstance.put("custom", true); // özel yemek işareti

        firestore.collection("Users")
                .document(userId)
                .collection("MealInstances")
                .document(instanceId)
                .set(mealInstance)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface OnMealInstanceAddedCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    public interface UserCallback {
        void onSuccess(Map<String, Object> userData);
        void onFailure(String error);
    }

    public void fetchUser(String userId, UserCallback callback) {
        firestore.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onSuccess(documentSnapshot.getData());
                    } else {
                        callback.onFailure("User not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface MealDetailCallback {
        void onSuccess(Map<String, Object> mealData);
        void onFailure(String error);
    }

    public void fetchMealById(String mealId, MealDetailCallback callback) {
        firestore.collection("Meal")
                .document(mealId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onSuccess(documentSnapshot.getData());
                    } else {
                        callback.onFailure("Meal not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface MealListCallback {
        void onSuccess(List<Map<String, Object>> meals);
        void onFailure(String errorMessage);
    }

    public void getAllMeals(MealListCallback callback) {
        firestore.collection("Meal")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> meals = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> data = doc.getData();
                        data.put("id", doc.getId()); // mealId'yi ekle
                        meals.add(data);
                    }
                    callback.onSuccess(meals);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void addMealInstance(String mealId, int quantity, String mealCategory, DatabaseCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        CollectionReference mealCollection = firestore.collection("MealInstances")
                .document(userId)
                .collection(today)
                .document(mealCategory)
                .collection("Items");

        mealCollection.get().addOnSuccessListener(query -> {
            boolean found = false;
            for (DocumentSnapshot doc : query.getDocuments()) {
                if (mealId.equals(doc.getString("mealId"))) {
                    long existingQuantity = doc.getLong("quantity") != null ? doc.getLong("quantity") : 0;
                    long updatedQuantity = existingQuantity + quantity;

                    doc.getReference().update("quantity", updatedQuantity)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(callback::onFailure);
                    found = true;
                    break;
                }
            }

            if (!found) {
                Map<String, Object> mealInstance = new HashMap<>();
                mealInstance.put("mealId", mealId);
                mealInstance.put("quantity", quantity);

                mealCollection.add(mealInstance)
                        .addOnSuccessListener(unused -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure);
            }

        }).addOnFailureListener(callback::onFailure);
    }

    public interface DatabaseCallback {
        void onSuccess();
        void onFailure(Exception e);
    }


    public interface MealInstanceListCallback {
        void onSuccess(List<Map<String, Object>> mealEntries);
        void onFailure(String error);
    }

    public void getMealInstances(String userId, String date, String mealCategory, MealInstanceListCallback callback) {
        firestore.collection("MealInstances")
                .document(userId)
                .collection(date)
                .document(mealCategory)
                .collection("Items")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> list = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Map<String, Object> item = doc.getData();
                        list.add(item);
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }





    public void fetchMealInstancesForToday(String userId, String date, MealInstanceCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String[] mealTypes = {"Kahvaltı", "Öğle Yemeği", "Akşam Yemeği", "Ara Öğün"};
        List<Map<String, Object>> result = new ArrayList<>();
        int[] completed = {0};
        boolean[] failed = {false};

        for (String mealType : mealTypes) {
            firestore.collection("MealInstances")
                    .document(userId)
                    .collection(date)
                    .document(mealType)
                    .collection("Items")
                    .get()
                    .addOnSuccessListener(itemsSnapshot -> {
                        List<Map<String, Object>> meals = new ArrayList<>();
                        for (DocumentSnapshot itemDoc : itemsSnapshot.getDocuments()) {
                            Map<String, Object> itemData = itemDoc.getData();
                            if (itemData != null) {
                                meals.add(itemData);
                            }
                        }

                        if (!meals.isEmpty()) {
                            Map<String, Object> entry = new HashMap<>();
                            entry.put("mealType", mealType);
                            entry.put("meals", meals);
                            result.add(entry);
                        }

                        completed[0]++;
                        if (completed[0] == mealTypes.length && !failed[0]) {
                            callback.onSuccess(result);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (!failed[0]) {
                            failed[0] = true;
                            callback.onFailure(e.getMessage());
                        }
                    });
        }
    }


    public interface MealInstanceCallback {
        void onSuccess(List<Map<String, Object>> mealInstances);
        void onFailure(String error);
    }


}
