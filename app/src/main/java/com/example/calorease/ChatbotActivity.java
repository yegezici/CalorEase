package com.example.calorease;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ChatbotActivity extends AppCompatActivity {

    private Button buttonLast7Days;
    private Button buttonToday;
    private Button buttonAnalyze30;
    private TextView textChatbotResponse;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        buttonLast7Days = findViewById(R.id.buttonLast7);
        buttonToday = findViewById(R.id.buttonToday);
        buttonAnalyze30 = findViewById(R.id.buttonAnalyze30);
        textChatbotResponse = findViewById(R.id.text_chatbot_response);
        scrollView = findViewById(R.id.scroll_view_response);

        buttonLast7Days.setOnClickListener(v -> {
            Chatbot chatbot = new Chatbot();
            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            chatbot.askLast7DaysBasedOnCalorie(userId, new Chatbot.ChatbotCallback() {
                @Override
                public void onResponse(String response) {
                    runOnUiThread(() -> showChatbotResponse(response));
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> showChatbotResponse("Hata: " + error));
                }
            });
        });

        buttonToday.setOnClickListener(v -> {
            Chatbot chatbot = new Chatbot();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            chatbot.askTodayBasedOnCalorie(userId, new Chatbot.ChatbotCallback() {
                @Override
                public void onResponse(String response) {
                    runOnUiThread(() -> showChatbotResponse(response));
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> showChatbotResponse("Hata: " + error));
                }
            });
        });

        buttonAnalyze30.setOnClickListener(v -> {
            Chatbot chatbot = new Chatbot();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            chatbot.askMonthlyCalorieAdvice(userId, new Chatbot.ChatbotCallback() {
                @Override
                public void onResponse(String response) {
                    runOnUiThread(() -> showChatbotResponse(response));
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> showChatbotResponse("Hata: " + error));
                }
            });
        });
    }

    private void showChatbotResponse(String response) {
        textChatbotResponse.setText(response);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}
