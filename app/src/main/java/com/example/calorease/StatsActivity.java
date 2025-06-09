package com.example.calorease;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    private BarChart barChart;
    private TextView emojiFeedback, textCarbComment, textProteinComment, textFatComment, textTitle;

    private final int DAILY_GOAL_CALORIES = 2000;
    private final int DAILY_GOAL_CARB = 250;
    private final int DAILY_GOAL_PROTEIN = 60;
    private final int DAILY_GOAL_FAT = 70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        barChart = findViewById(R.id.bar_chart);
        emojiFeedback = findViewById(R.id.emoji_feedback);
        textCarbComment = findViewById(R.id.text_carb_comment);
        textProteinComment = findViewById(R.id.text_protein_comment);
        textFatComment = findViewById(R.id.text_fat_comment);
        textTitle = findViewById(R.id.text_stats_title);

        fetchWeeklyData();
    }

    private void fetchWeeklyData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseManager.getInstance().fetchLast7DaysSummaries(userId, new DatabaseManager.MealStatsListCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> summaries) {
                List<BarEntry> calorieEntries = new ArrayList<>();
                List<String> days = new ArrayList<>();

                double totalCarbs = 0;
                double totalFat = 0;
                double totalProtein = 0;
                double totalCalories = 0;

                for (int i = 0; i < summaries.size(); i++) {
                    Map<String, Object> day = summaries.get(i);
                    float cal = ((Number) day.get("totalCalories")).floatValue();
                    float carbs = ((Number) day.get("totalCarbs")).floatValue();
                    float protein = ((Number) day.get("totalProtein")).floatValue();
                    float fat = ((Number) day.get("totalFat")).floatValue();

                    calorieEntries.add(new BarEntry(i, cal));
                    days.add(String.format(Locale.getDefault(), "Gün %d", 7 - i));

                    totalCalories += cal;
                    totalCarbs += carbs;
                    totalProtein += protein;
                    totalFat += fat;
                }

                float avgCalories = (float) (totalCalories / summaries.size());
                float avgCarbs = (float) (totalCarbs / summaries.size());
                float avgProtein = (float) (totalProtein / summaries.size());
                float avgFat = (float) (totalFat / summaries.size());

                renderChart(calorieEntries, days);
                renderEmojiFeedback(avgCalories);
                renderComments(avgCarbs, avgProtein, avgFat);
            }

            @Override
            public void onFailure(String error) {
                emojiFeedback.setText("Veriler alınamadı 😢");
            }
        });
    }

    private void renderChart(List<BarEntry> entries, List<String> days) {
        BarDataSet dataSet = new BarDataSet(entries, "Kalori");
        dataSet.setColor(Color.parseColor("#66BB6A")); // Açık yeşil
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);

        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(true);
        yAxisLeft.setAxisMinimum(0f);

        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void renderEmojiFeedback(float avgCalories) {
        String emoji;
        if (avgCalories < DAILY_GOAL_CALORIES * 0.6) {
            emoji = "🔴 Çok düşük kalori alımı";
        } else if (avgCalories < DAILY_GOAL_CALORIES * 1.2) {
            emoji = "🟢 Harika denge!";
        } else {
            emoji = "🟡 Fazla kalori alımı";
        }
        emojiFeedback.setText("Genel Durum: " + emoji);
    }

    private void renderComments(float carbs, float protein, float fat) {
        textCarbComment.setText(compareMacro("Karbonhidrat", carbs, DAILY_GOAL_CARB));
        textProteinComment.setText(compareMacro("Protein", protein, DAILY_GOAL_PROTEIN));
        textFatComment.setText(compareMacro("Yağ", fat, DAILY_GOAL_FAT));
    }

    private String compareMacro(String name, float value, int goal) {
        if (value > goal * 1.2) {
            return name + " alımın bu hafta yüksekti!";
        } else if (value < goal * 0.6) {
            return name + " alımın düşük kaldı.";
        } else {
            return name + " alımın dengeliydi 👌";
        }
    }
}
