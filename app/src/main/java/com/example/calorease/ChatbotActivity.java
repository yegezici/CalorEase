package com.example.calorease;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
                // Chatbot nesnesini burada oluştur
                Chatbot chatbot = new Chatbot();

                chatbot.getChatbotReply("Bana diyet önerisi ver", new Chatbot.ChatbotCallback() {
                    @Override
                    public void onResponse(String response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textChatbotResponse.setText(response);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textChatbotResponse.setText("Hata: " + error);
                            }
                        });
                    }
                });
            }
        });

    }
}
