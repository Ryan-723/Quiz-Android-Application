package com.example.group2_final_project.user.ui.setting;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.group2_final_project.MainActivity;
import com.example.group2_final_project.R;
import com.example.group2_final_project.auth.LoginActivity;
import com.example.group2_final_project.databinding.FragmentSettingBinding;
import com.example.group2_final_project.user.AboutUs;
import com.example.group2_final_project.user.Dashboard;
import com.example.group2_final_project.user.ui.dashboard.DashboardFragment;
import com.example.group2_final_project.user.ui.profile.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingFragment extends Fragment {
    FragmentSettingBinding binding;
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        binding.aboutUsPageCard.setOnClickListener(v -> {
            Intent aboutIntent = new Intent(getActivity(), AboutUs.class);
            startActivity(aboutIntent);
        });

        binding.homePageCard.setOnClickListener(v -> {
            Dashboard dashboard = (Dashboard) getActivity();
            dashboard.loadFragment(new DashboardFragment());
        });

        binding.profileCard.setOnClickListener(v -> {
            Dashboard dashboard = (Dashboard) getActivity();
            dashboard.loadFragment(new ProfileFragment());
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        binding.logoutCard.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        setUserData();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUserData() {
        binding.userWelcomeName.setText("Hey " + firebaseUser.getDisplayName());
        binding.userFullName.setText(firebaseUser.getDisplayName());
        binding.userEmail.setText(firebaseUser.getEmail());
        Glide.with(getActivity())
                .load(firebaseUser.getPhotoUrl())
                .into(binding.userProfileImage);
    }
}