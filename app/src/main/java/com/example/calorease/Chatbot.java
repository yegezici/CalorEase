package com.example.calorease;


import java.util.List;
import java.util.Map;



import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class Chatbot {

    private static final String apiKey = BuildConfig.OPENAI_API_KEY;
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public interface ChatbotCallback {
        void onResponse(String response);
        void onError(String error);
    }



    public void getChatbotReply(String userMessage, ChatbotCallback callback) {
        OkHttpClient client = new OkHttpClient();

        JSONObject requestBody = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        try {
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", userMessage);

            messagesArray.put(message);

            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", messagesArray);
            requestBody.put("temperature", 0.7);
        } catch (Exception e) {
            callback.onError("JSON oluşturulamadı: " + e.getMessage());
            return;
        }

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("API bağlantı hatası: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("API hatası: " + response.code());
                    return;
                }

                String responseBody = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseBody);
                    JSONArray choices = json.getJSONArray("choices");
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    String reply = message.getString("content");
                    callback.onResponse(reply.trim());
                } catch (Exception e) {
                    callback.onError("Yanıt ayrıştırılamadı: " + e.getMessage());
                }
            }
        });
    }

    public void askLast7DaysBasedOnCalorie(String userId, ChatbotCallback callback) {
        DatabaseManager.getInstance().fetchLast7DaysSummaries(userId, new DatabaseManager.MealStatsListCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> summaries) {
                StringBuilder prompt = new StringBuilder();
                prompt.append("Aşağıda bir kullanıcının son 7 güne ait günlük kalori ve makro (protein, karbonhidrat, yağ) değerleri verilmiştir:\n");

                for (Map<String, Object> day : summaries) {
                    prompt.append("Tarih: ").append(day.get("date"))
                            .append(" | Kalori: ").append(day.get("totalCalories"))
                            .append(" kcal, Protein: ").append(day.get("totalProtein"))
                            .append(" g, Karbonhidrat: ").append(day.get("totalCarbs"))
                            .append(" g, Yağ: ").append(day.get("totalFat"))
                            .append(" g\n");
                }

                prompt.append("\nBu verilere göre kullanıcının beslenme alışkanlıklarını değerlendir ve 2-3 öneride bulun.");

                getChatbotReply(prompt.toString(), callback);
            }

            @Override
            public void onFailure(String error) {
                callback.onError("Veriler alınamadı: " + error);
            }
        });
    }





}
