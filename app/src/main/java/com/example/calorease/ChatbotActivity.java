package com.example.calorease;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ChatbotActivity extends AppCompatActivity {

    private Button buttonRequestSuggestion;
    private TextView textChatbotResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        buttonRequestSuggestion = findViewById(R.id.button_request_suggestion);
        textChatbotResponse = findViewById(R.id.text_chatbot_response);

        buttonRequestSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chatbot chatbot = new Chatbot();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
    }
}
