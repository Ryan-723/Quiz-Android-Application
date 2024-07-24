package com.example.group2_final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.group2_final_project.onboarding.Onboarding;

public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    Intent introIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                introIntent = new Intent(MainActivity.this, Onboarding.class);
                startActivity(introIntent);
                finish();
            }
        }, 1500); // Delay duration before starting onboarding
    }
}