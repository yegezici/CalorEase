package com.example.calorease;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddWaterActivity extends AppCompatActivity {

    private EditText editAmount;
    private View viewWaterLevel;
    private final int DAILY_WATER_GOAL = 2000; // ml

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_water);

        editAmount = findViewById(R.id.edit_amount);
        viewWaterLevel = findViewById(R.id.viewWaterLevel);

        Button btnAddWater = findViewById(R.id.btn_add_water);
        btnAddWater.setOnClickListener(v -> addWater());

        // Güncel su miktarını göster
        fetchAndUpdateBottle();
    }

    private void fetchAndUpdateBottle() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DatabaseManager.getInstance().fetchWaterIntake(userId, today, new DatabaseManager.OnWaterFetchedCallback() {
            @Override
            public void onSuccess(int amount) {
                updateWaterBottle(amount);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AddWaterActivity.this, "Su miktarı alınamadı", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addWater() {
        String amountStr = editAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Lütfen bir miktar girin", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        if (amount <= 0) {
            Toast.makeText(this, "Geçerli bir miktar girin", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseManager.getInstance().addWaterIntake(userId, amount, new DatabaseManager.OnWaterUpdatedCallback() {
            @Override
            public void onSuccess(int totalAmount) {
                updateWaterBottle(totalAmount);
                Toast.makeText(AddWaterActivity.this, "Su eklendi!", Toast.LENGTH_SHORT).show();
                editAmount.setText("");
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AddWaterActivity.this, "Hata: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWaterBottle(int currentWater) {
        View container = findViewById(R.id.waterBottleContainer);

        container.post(() -> {
            int maxHeight = container.getHeight();
            float percentage = Math.min(1f, currentWater / (float) DAILY_WATER_GOAL);
            int waterHeight = (int) (maxHeight * percentage);

            ViewGroup.LayoutParams params = viewWaterLevel.getLayoutParams();
            params.height = waterHeight;
            viewWaterLevel.setLayoutParams(params);
        });
    }
}
