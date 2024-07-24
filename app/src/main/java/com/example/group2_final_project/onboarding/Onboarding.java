package com.example.group2_final_project.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toolbar;

import com.example.group2_final_project.R;
import com.example.group2_final_project.auth.LoginActivity;
import com.example.group2_final_project.databinding.ActivityOnboardingBinding;
import com.example.group2_final_project.onboarding.OnboardingScreenItem;
import com.example.group2_final_project.onboarding.OnboardingViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Onboarding extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    ActivityOnboardingBinding onboardingBinding;
    private ViewPager2 onboardingPager;
    OnboardingViewPagerAdapter onboardingViewPagerAdapter;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onboardingBinding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        View view = onboardingBinding.getRoot();
        setContentView(view);

        sharedPreferences = getSharedPreferences("ONBOARDING", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("onboarding_completed") && sharedPreferences.getBoolean("onboarding_completed", false)) {
            redirectToLogin();
        }

        Window window = getWindow();
        List<OnboardingScreenItem> screenItemList = new ArrayList<>();
        screenItemList.add(new OnboardingScreenItem(R.drawable.onboarding_1));
        screenItemList.add(new OnboardingScreenItem(R.drawable.onboarding_2));
        screenItemList.add(new OnboardingScreenItem(R.drawable.onboarding_3));
        screenItemList.add(new OnboardingScreenItem(R.drawable.onboarding_4));
        screenItemList.add(new OnboardingScreenItem(R.drawable.onboarding_5));

        onboardingPager = findViewById(R.id.onboardingViewPager);
        onboardingViewPagerAdapter = new OnboardingViewPagerAdapter(screenItemList);
        onboardingPager.setAdapter(onboardingViewPagerAdapter);
        onboardingPager.setClipToPadding(false);
        onboardingPager.setClipChildren(false);
        onboardingPager.setOffscreenPageLimit(2);

        onboardingPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        onboardingPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateStatusBarColor(position);
            }
        });

        onboardingBinding.nextButton.setOnClickListener(this);
        onboardingBinding.skipButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == onboardingBinding.nextButton.getId()) {
            int currentPage = onboardingPager.getCurrentItem();
            if (currentPage == 4) {
                setOnboardingCompleted();
            } else {
                onboardingPager.setCurrentItem(currentPage + 1, true);
            }
        }

        if (view.getId() == onboardingBinding.skipButton.getId()) {
            setOnboardingCompleted();
        }
    }
    // Mark onboarding as completed in SharedPreferences and redirect to login
    private void setOnboardingCompleted() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("onboarding_completed", true);
        editor.commit();

        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent loginIntent = new Intent(Onboarding.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void updateStatusBarColor(int position) {
        Window window = getWindow();

        // Check if the device is running on Android Lollipop or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch (position) {
                case 0:
                    // Change status bar color for the first page

                    window.setStatusBarColor(getResources().getColor(R.color.onboarding_1));
                    onboardingBinding.nextButton.setBackgroundColor(getResources().getColor(R.color.onboarding_1));
                    onboardingBinding.nextButton.setText("Next");

                    break;
                case 1:
                    // Change status bar color for the second page
                    window.setStatusBarColor(getResources().getColor(R.color.onboarding_2));
                    onboardingBinding.nextButton.setBackgroundColor(getResources().getColor(R.color.onboarding_2));
                    onboardingBinding.nextButton.setText("Next");

                    break;
                case 2:
                    // Change status bar color for the second page
                    window.setStatusBarColor(getResources().getColor(R.color.onboarding_3));
                    onboardingBinding.nextButton.setBackgroundColor(getResources().getColor(R.color.onboarding_3));
                    onboardingBinding.nextButton.setText("Next");

                    break;
                case 3:
                    // Change status bar color for the second page
                    window.setStatusBarColor(getResources().getColor(R.color.onboarding_4));
                    onboardingBinding.nextButton.setBackgroundColor(getResources().getColor(R.color.onboarding_4));
                    onboardingBinding.nextButton.setText("Next");

                    break;
                case 4:
                    // Change status bar color for the second page
                    window.setStatusBarColor(getResources().getColor(R.color.onboarding_5));
                    onboardingBinding.nextButton.setBackgroundColor(getResources().getColor(R.color.onboarding_5));
                    onboardingBinding.nextButton.setText("Finish");
                    break;
                // Add more cases for additional pages if needed
                default:
                    break;
            }
        }
    }
}