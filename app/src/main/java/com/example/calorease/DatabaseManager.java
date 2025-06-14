package com.example.calorease;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import com.google.firebase.firestore.CollectionReference;




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

    public void addCustomMealInstance(String userId, String date, String mealType,
                                      String name, int calories, int protein, int carbs, int fat, int quantity,
                                      OnMealInstanceAddedCallback callback) {

        Map<String, Object> mealEntry = new HashMap<>();
        mealEntry.put("name", name);
        mealEntry.put("calories", calories);
        mealEntry.put("protein", protein);
        mealEntry.put("carb", carbs);
        mealEntry.put("fat", fat);
        mealEntry.put("quantity", quantity);
        mealEntry.put("custom", true);

        firestore.collection("MealInstances")
                .document(userId)
                .collection(date)
                .document(mealType)
                .collection("Items")
                .add(mealEntry)
                .addOnSuccessListener(unused -> {
                    // Toplam değerleri güncelle


                    updateTotalStats(calories, carbs, protein, fat, new DatabaseCallback() {
                        @Override
                        public void onSuccess() {
                            callback.onSuccess();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            callback.onFailure(e.getMessage());
                        }
                    });
                })
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
                        data.put("id", doc.getId());
                        meals.add(data);
                    }
                    callback.onSuccess(meals);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void addMealInstance(String mealId, int quantity,
                                double calories, double carbs, double protein, double fat,
                                String mealCategory, DatabaseCallback callback) {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DocumentReference categoryDocRef = firestore.collection("MealInstances")
                .document(userId)
                .collection(today)
                .document(mealCategory);

        CollectionReference mealItemsRef = categoryDocRef.collection("Items");

        mealItemsRef.get().addOnSuccessListener(query -> {
            boolean found = false;

            for (DocumentSnapshot doc : query.getDocuments()) {
                if (mealId.equals(doc.getString("mealId"))) {
                    long existingQuantity = doc.getLong("quantity") != null ? doc.getLong("quantity") : 0;
                    long updatedQuantity = existingQuantity + quantity;

                    doc.getReference().update("quantity", updatedQuantity)
                            .addOnSuccessListener(unused -> updateTotalStats(calories, carbs, protein, fat, callback))
                            .addOnFailureListener(callback::onFailure);
                    found = true;
                    break;
                }
            }

            if (!found) {
                Map<String, Object> mealInstance = new HashMap<>();
                mealInstance.put("mealId", mealId);
                mealInstance.put("quantity", quantity);

                mealItemsRef.add(mealInstance)
                        .addOnSuccessListener(unused -> updateTotalStats(calories, carbs, protein, fat, callback))
                        .addOnFailureListener(callback::onFailure);
            }

        }).addOnFailureListener(callback::onFailure);

    }



    private void updateTotalStats(double addedCalories, double addedCarbs, double addedProtein, double addedFat, DatabaseCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DocumentReference summaryDocRef = firestore
                .collection("MealInstances")
                .document(userId)
                .collection(today)
                .document("summary");

        summaryDocRef.get().addOnSuccessListener(doc -> {
            double currentCalories = 0.0;
            double currentCarbs = 0.0;
            double currentProtein = 0.0;
            double currentFat = 0.0;

            if (doc.exists()) {
                currentCalories = getDoubleOrZero(doc, "totalCalories");
                currentCarbs = getDoubleOrZero(doc, "totalCarbs");
                currentProtein = getDoubleOrZero(doc, "totalProtein");
                currentFat = getDoubleOrZero(doc, "totalFat");
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("totalCalories", currentCalories + addedCalories);
            updates.put("totalCarbs", currentCarbs + addedCarbs);
            updates.put("totalProtein", currentProtein + addedProtein);
            updates.put("totalFat", currentFat + addedFat);

            summaryDocRef.set(updates, SetOptions.merge())
                    .addOnSuccessListener(unused -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);

        }).addOnFailureListener(callback::onFailure);
    }

    private double getDoubleOrZero(DocumentSnapshot doc, String key) {
        Object value = doc.get(key);
        return (value instanceof Number) ? ((Number) value).doubleValue() : 0.0;
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

                        Map<String, Object> entry = new HashMap<>();
                        entry.put("mealType", mealType);
                        entry.put("meals", meals);
                        result.add(entry);

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

    // --- Water tracking methods ---
    public void fetchWaterIntake(String userId, String date, OnWaterFetchedCallback callback) {
        firestore.collection("Users")
                .document(userId)
                .collection("WaterIntake")
                .document(date)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        long amount = doc.contains("amount") ? doc.getLong("amount") : 0;
                        callback.onSuccess((int) amount);
                    } else {
                        callback.onSuccess(0);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void addWaterIntake(String userId, int amount, OnWaterUpdatedCallback callback) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DocumentReference waterDoc = firestore.collection("Users")
                .document(userId)
                .collection("WaterIntake")
                .document(today);

        waterDoc.get().addOnSuccessListener(snapshot -> {
            long current = snapshot.exists() && snapshot.contains("amount") ? snapshot.getLong("amount") : 0;
            long updated = current + amount;

            Map<String, Object> data = new HashMap<>();
            data.put("amount", updated);

            waterDoc.set(data).addOnSuccessListener(unused -> callback.onSuccess((int) updated))
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface OnWaterFetchedCallback {
        void onSuccess(int amount);
        void onFailure(String errorMessage);
    }

    public interface OnWaterUpdatedCallback {
        void onSuccess(int totalAmount);
        void onFailure(String errorMessage);
    }

    public interface MealInstanceCallback {
        void onSuccess(List<Map<String, Object>> mealInstances);
        void onFailure(String error);
    }



    public void fetchTodaySummary(String userId, String date, MealStatsCallback callback) {
        FirebaseFirestore.getInstance()
                .collection("MealInstances")
                .document(userId)
                .collection(date)
                .document("summary")
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        double calories = toDouble(doc.get("totalCalories"));
                        double carbs = toDouble(doc.get("totalCarbs"));
                        double protein = toDouble(doc.get("totalProtein"));
                        double fat = toDouble(doc.get("totalFat"));
                        callback.onSuccess(calories, carbs, protein, fat);
                    } else {
                        callback.onSuccess(0, 0, 0, 0);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface MealStatsCallback {
        void onSuccess(double calories, double carbs, double protein, double fat);
        void onFailure(String error);
    }
    private double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception e) {
            return 0.0;
        }
    }


    public void fetchLast7DaysSummaries(String userId, MealStatsListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> last7Dates = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            last7Dates.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DATE, -1);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        final int[] pending = {last7Dates.size()};

        for (String date : last7Dates) {
            db.collection("MealInstances")
                    .document(userId)
                    .collection(date)
                    .document("summary")
                    .get()
                    .addOnSuccessListener(doc -> {
                        Map<String, Object> day = new HashMap<>();
                        day.put("date", date);
                        day.put("totalCalories", getDoubleOrZero(doc.get("totalCalories")));
                        day.put("totalCarbs", getDoubleOrZero(doc.get("totalCarbs")));
                        day.put("totalProtein", getDoubleOrZero(doc.get("totalProtein")));
                        day.put("totalFat", getDoubleOrZero(doc.get("totalFat")));
                        result.add(day);

                        pending[0]--;
                        if (pending[0] == 0) {
                            callback.onSuccess(result);
                        }
                    })
                    .addOnFailureListener(e -> {
                        pending[0]--;
                        if (pending[0] == 0) {
                            callback.onSuccess(result); // eksik gün varsa yine gönder
                        }
                    });
        }
    }

    private double getDoubleOrZero(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }


    public interface MealStatsListCallback {
        void onSuccess(List<Map<String, Object>> summaries);
        void onFailure(String error);
    }
    public void fetchTodaySummary(String userId, MealStatsCallback2 callback) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String today = sdf.format(Calendar.getInstance().getTime());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("MealInstances")
                .document(userId)
                .collection(today)
                .document("summary")
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("date", today);
                        data.put("totalCalories", getDoubleOrZero(doc.get("totalCalories")));
                        data.put("totalProtein", getDoubleOrZero(doc.get("totalProtein")));
                        data.put("totalCarbs", getDoubleOrZero(doc.get("totalCarbs")));
                        data.put("totalFat", getDoubleOrZero(doc.get("totalFat")));
                        callback.onSuccess(data);
                    } else {
                        callback.onFailure("Bugünün summary verisi bulunamadı.");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Veri alınırken hata oluştu: " + e.getMessage());
                });
    }

    // Yardımcı fonksiyon:

    // Callback arayüzü:
    public interface MealStatsCallback2 {
        void onSuccess(Map<String, Object> summary);
        void onFailure(String error);
    }


    public void fetchMonthlyCaloriesWithGoal(String userId, MonthlyWithGoalCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists() && userDoc.contains("dailyCalorieGoal")) {
                        double calorieGoal = userDoc.getDouble("dailyCalorieGoal");

                        // 30 gün oluştur
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        Calendar calendar = Calendar.getInstance();
                        List<String> last30Days = new ArrayList<>();
                        for (int i = 0; i < 30; i++) {
                            last30Days.add(sdf.format(calendar.getTime()));
                            calendar.add(Calendar.DATE, -1);
                        }

                        List<Double> dailyCalories = new ArrayList<>();
                        final int[] completed = {0};

                        for (String date : last30Days) {
                            db.collection("MealInstances")
                                    .document(userId)
                                    .collection(date)
                                    .document("summary")
                                    .get()
                                    .addOnSuccessListener(doc -> {
                                        dailyCalories.add(getDoubleOrZero(doc.get("totalCalories")));
                                        completed[0]++;
                                        if (completed[0] == last30Days.size()) {
                                            callback.onSuccess(calorieGoal, dailyCalories);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        dailyCalories.add(0.0);
                                        completed[0]++;
                                        if (completed[0] == last30Days.size()) {
                                            callback.onSuccess(calorieGoal, dailyCalories);
                                        }
                                    });
                        }
                    } else {
                        callback.onFailure("Kullanıcı hedefi (dailyCalorieGoal) bulunamadı.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Kullanıcı verisi alınamadı: " + e.getMessage()));
    }
    public interface MonthlyWithGoalCallback {
        void onSuccess(double calorieGoal, List<Double> dailyCalories);
        void onFailure(String error);
    }


}
