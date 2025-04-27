package com.example.calorease;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DietPagerAdapter extends FragmentStateAdapter {

    private String selectedDate;

    public DietPagerAdapter(@NonNull FragmentActivity fragmentActivity, String selectedDate) {
        super(fragmentActivity);
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return MealListFragment.newInstance("Kahvaltı", selectedDate);
            case 1:
                return MealListFragment.newInstance("Öğle Yemeği", selectedDate);
            case 2:
                return MealListFragment.newInstance("Akşam Yemeği", selectedDate);
            case 3:
                return MealListFragment.newInstance("Atıştırmalık", selectedDate);
            default:
                return MealListFragment.newInstance("Kahvaltı", selectedDate);
        }
    }

    @Override
    public int getItemCount() {
        return 4; // 4 sekme: Kahvaltı, Öğle, Akşam, Atıştırmalık
    }



    public void updateDate(String newDate) {
        this.selectedDate = newDate;
        notifyDataSetChanged();
    }

}
