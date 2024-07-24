package com.example.group2_final_project.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.group2_final_project.R;
import com.example.group2_final_project.databinding.ActivityAboutUsBinding;
import com.example.group2_final_project.databinding.ActivityQuizDetailBinding;
import com.example.group2_final_project.user.ui.profile.ProfileFragment;

public class AboutUs extends AppCompatActivity {
 ActivityAboutUsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.backButton.setOnClickListener(v -> {
            finish();
        });
    }
}