package com.example.group2_final_project.user.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.group2_final_project.R;
import com.example.group2_final_project.databinding.FragmentProfileBinding;
import com.example.group2_final_project.databinding.FragmentSettingBinding;
import com.example.group2_final_project.helpers.SweetAlert;
import com.example.group2_final_project.user.AboutUs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileFragment extends Fragment {
    private FirebaseUser firebaseUser;
    FragmentProfileBinding binding;

    SweetAlert sweetAlert;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        sweetAlert = new SweetAlert(getActivity());
//        binding.aboutUsPageCard.setOnClickListener(v -> {
//            Intent aboutIntent = new Intent(getActivity(), AboutUs.class);
//            startActivity(aboutIntent);
//        });

        binding.btnSaveChanges.setOnClickListener(v -> {
            String full_name = binding.fullName.getText().toString();
            String emailAddress = binding.emailAddress.getText().toString();
            saveUserData(firebaseUser, full_name, emailAddress);
            sweetAlert.showSuccessAlert("Profile Updated", "Your profile has been updated successfully", "OK");
        });

        setUserData();
        return  binding.getRoot();
    }

    private void setUserData() {
        binding.userNameHead.setText(firebaseUser.getDisplayName());

        binding.fullName.setText(firebaseUser.getDisplayName());
        binding.emailAddress.setText(firebaseUser.getEmail());
        Glide.with(getActivity())
                .load(firebaseUser.getPhotoUrl())
                .into(binding.userProfileImage);
    }

    private void saveUserData(FirebaseUser user, String full_name, String emailAddress) {
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(full_name)
                    .build();
            user.updateProfile(profileUpdates);

            binding.userNameHead.setText(full_name);
        }
    }
}