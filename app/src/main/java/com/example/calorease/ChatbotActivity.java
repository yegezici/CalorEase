package com.example.calorease;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ChatbotActivity extends AppCompatActivity {

    private Button buttonLast7Days;
    private Button buttonToday;
    private Button buttonAnalyze30;
    private Button button;
    private TextView textChatbotResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        buttonLast7Days = findViewById(R.id.buttonLast7);
        buttonToday = findViewById(R.id.buttonToday);
        buttonAnalyze30 = findViewById(R.id.buttonAnalyze30);
        button = findViewById(R.id.button1);
        textChatbotResponse = findViewById(R.id.text_chatbot_response);

        buttonLast7Days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chatbot chatbot = new Chatbot();
                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                chatbot.askLast7DaysBasedOnCalorie(userId, new Chatbot.ChatbotCallback() {
                    @Override
                    public void onResponse(String response) {
                        runOnUiThread(() -> textChatbotResponse.setText(response));
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> textChatbotResponse.setText("Hata: " + error));
                    }
                });
            }
        });



        buttonToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chatbot chatbot = new Chatbot();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                chatbot.askTodayBasedOnCalorie(userId, new Chatbot.ChatbotCallback() {
                    @Override
                    public void onResponse(String response) {
                        runOnUiThread(() -> textChatbotResponse.setText(response));
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> textChatbotResponse.setText("Hata: " + error));
                    }
                });
            }
        });


    }
}
