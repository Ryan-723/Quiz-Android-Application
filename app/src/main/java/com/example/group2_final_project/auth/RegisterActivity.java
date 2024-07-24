package com.example.group2_final_project.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.group2_final_project.databinding.ActivityRegisterBinding;
import com.example.group2_final_project.helpers.SweetAlert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityRegisterBinding registerBinding;
    SweetAlert sweetAlert;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = registerBinding.getRoot();
        setContentView(view);

        sweetAlert = new SweetAlert(this);

        // Initialize Firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance();
        setListeners();
    }


    private void setListeners() {
        registerBinding.textLogin.setOnClickListener(this);
        registerBinding.buttonSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == registerBinding.textLogin.getId()) {
            Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(registerIntent);
            finish();
        }

        if (view.getId() == registerBinding.buttonSignup.getId()) {
            if (validateForm()) {
                registerUser();
            }
        }
    }

    private void registerUser() {
        String email = registerBinding.emailAddress.getText().toString().trim();
        String password = registerBinding.password.getText().toString().trim();
        String fullName = registerBinding.fullName.getText().toString().trim();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        saveUserData(user, fullName);
                        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("Acount Created").setContentText("Welcome to the world of QUIZ HERO")
                                .setConfirmText("Let's Go")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(loginIntent);
                                        finish();
                                    }
                                }).show();

                        clearTheForm();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearTheForm() {
        registerBinding.password.setText("");
        registerBinding.fullName.setText("");
        registerBinding.emailAddress.setText("");
    }

    // Save user data like display name (full name) in Firebase user profile
    private void saveUserData(FirebaseUser user, String full_name) {
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(full_name)
                    .build();
            user.updateProfile(profileUpdates);
        }
    }

    private boolean validateForm() {
        boolean validated = true;
        // Validate the registration form
        if (registerBinding.fullName.getText().toString().trim().isEmpty()) {
            registerBinding.fullName.setError("Please enter full name");
            sweetAlert.showErrorAlert("Error", "Please enter full name", "Ok");
            return false;
        }

        if (registerBinding.emailAddress.getText().toString().trim().isEmpty()) {
            registerBinding.emailAddress.setError("Please enter email address");
            sweetAlert.showErrorAlert("Error", "Please enter email address", "Ok");
            return false;
        }

        if (registerBinding.password.getText().toString().trim().isEmpty()) {
            registerBinding.password.setError("Please enter password");
            sweetAlert.showErrorAlert("Error", "Please enter password", "Ok");
        }

        return validated;
    }
}