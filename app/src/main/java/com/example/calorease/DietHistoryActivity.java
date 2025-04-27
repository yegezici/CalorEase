package com.example.calorease;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DietHistoryActivity extends AppCompatActivity {

    private TextView textTodayDate;
    private Button buttonSelectDate;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private DietPagerAdapter pagerAdapter;

    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_history);

        textTodayDate = findViewById(R.id.text_selected_date);
        buttonSelectDate = findViewById(R.id.button_select_date);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        selectedDate = getTodayDatabaseFormat();
        textTodayDate.setText("Tarih: " + getTodayFormatted());

        pagerAdapter = new DietPagerAdapter(this, selectedDate);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Kahvaltı");
                    break;
                case 1:
                    tab.setText("Öğle Yemeği");
                    break;
                case 2:
                    tab.setText("Akşam Yemeği");
                    break;
                case 3:
                    tab.setText("Atıştırmalık");
                    break;
            }
        }).attach();

        buttonSelectDate.setOnClickListener(v -> openDatePicker());
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    String displayDate = String.format(Locale.getDefault(), "%d %s %d", dayOfMonth,
                            calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()), year);
                    textTodayDate.setText("Tarih: " + displayDate);

                    // YENİ: Adapter'ı tamamen yeniden oluştur
                    pagerAdapter = new DietPagerAdapter(this, selectedDate);
                    viewPager.setAdapter(pagerAdapter);

                    new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                        switch (position) {
                            case 0:
                                tab.setText("Kahvaltı");
                                break;
                            case 1:
                                tab.setText("Öğle Yemeği");
                                break;
                            case 2:
                                tab.setText("Akşam Yemeği");
                                break;
                            case 3:
                                tab.setText("Atıştırmalık");
                                break;
                        }
                    }).attach();

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private String getTodayFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("tr", "TR"));
        return sdf.format(Calendar.getInstance().getTime());
    }

    private String getTodayDatabaseFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }
}