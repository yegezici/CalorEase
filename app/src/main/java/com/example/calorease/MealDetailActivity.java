package com.example.calorease;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MealDetailActivity extends AppCompatActivity {

    private TextView mealNameTextView;
    private EditText quantityEditText;
    private Button addMealButton;

    private String mealId;
    private String mealName;
    private String mealCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);

        mealNameTextView = findViewById(R.id.text_meal_name);
        quantityEditText = findViewById(R.id.edit_text_quantity);
        addMealButton = findViewById(R.id.button_add_meal);

        mealId = getIntent().getStringExtra("mealId");
        mealName = getIntent().getStringExtra("mealName");
        mealCategory = getIntent().getStringExtra("mealCategory");

        mealNameTextView.setText(mealName);

        addMealButton.setOnClickListener(v -> {
            String quantityText = quantityEditText.getText().toString().trim();

            if (quantityText.isEmpty()) {
                Toast.makeText(this, "Lütfen gram miktarını giriniz.", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            saveMealToDatabase(mealId, quantity);
        });
    }

    private void saveMealToDatabase(String mealId, int quantity) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userMealsRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("Meals")
                .child(getTodayDate())
                .child(mealCategory);

        userMealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean mealExists = false;
                String existingMealKey = null;
                long existingQuantity = 0;

                for (DataSnapshot mealSnapshot : snapshot.getChildren()) {
                    String existingMealId = mealSnapshot.child("mealId").getValue(String.class);
                    if (existingMealId != null && existingMealId.equals(mealId)) {
                        mealExists = true;
                        existingMealKey = mealSnapshot.getKey();
                        Long quantityLong = mealSnapshot.child("quantity").getValue(Long.class);
                        existingQuantity = (quantityLong != null) ? quantityLong : 0;
                        break;
                    }
                }

                if (mealExists && existingMealKey != null) {
                    long updatedQuantity = existingQuantity + quantity;
                    userMealsRef.child(existingMealKey).child("quantity").setValue(updatedQuantity)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(MealDetailActivity.this, mealName + " miktarı güncellendi!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MealDetailActivity.this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    String pushId = userMealsRef.push().getKey();
                    if (pushId != null) {
                        userMealsRef.child(pushId).child("mealId").setValue(mealId);
                        userMealsRef.child(pushId).child("quantity").setValue(quantity)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(MealDetailActivity.this, mealName + " " + quantity + "g eklendi!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(MealDetailActivity.this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MealDetailActivity.this, "Veri ekleme iptal edildi!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
