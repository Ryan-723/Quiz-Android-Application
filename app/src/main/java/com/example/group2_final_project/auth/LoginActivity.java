package com.example.group2_final_project.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.group2_final_project.R;
import com.example.group2_final_project.admin.AdminDashboard;
import com.example.group2_final_project.databinding.ActivityLoginBinding;
import com.example.group2_final_project.helpers.SweetAlert;
import com.example.group2_final_project.user.Dashboard;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityLoginBinding loginBinding;
    SweetAlert sweetAlert;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser loggedInUser;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        loggedInUser = firebaseAuth.getCurrentUser();
        if (loggedInUser != null) {
            checkIfAvatarIsUploaded();
        }

        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = loginBinding.getRoot();
        setContentView(view);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        sweetAlert = new SweetAlert(this);
        setListeners();
    }

    private void checkIfAvatarIsUploaded() {
        if (loggedInUser.getEmail().equals("admin@quizhero.com")) {
            redirectToOtherPage(new AdminDashboard());
        } else if (loggedInUser.getPhotoUrl() == null) {
            redirectToOtherPage(new AvatarActivity());
        } else {
            redirectToOtherPage(new Dashboard());
        }
    }

    private void setListeners() {
        loginBinding.txtSignup.setOnClickListener(this);
        loginBinding.buttonLogin.setOnClickListener(this);
        loginBinding.googleLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == loginBinding.txtSignup.getId()) {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        }

        if (view.getId() == loginBinding.buttonLogin.getId()) {
            if (validateForm()) {
                loginUser();
            }
        }

        if (view.getId() == loginBinding.googleLogin.getId()) {
            signInWithGoogle();
        }
    }

    private void signInWithGoogle() {
        // Handle button clicks based on their IDs
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        // Handle the result of Google sign-in and authenticate with Firebase
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Log.w("GoogleSignIn", "Google sign in failed", e);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        loggedInUser = firebaseAuth.getCurrentUser();
                        checkIfAvatarIsUploaded();
                        // Update UI or navigate to the main screen
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("GoogleSignIn", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void loginUser() {
        String email = loginBinding.emailAddress.getText().toString().trim();
        String password = loginBinding.password.getText().toString().trim();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        loggedInUser = firebaseAuth.getCurrentUser();
                        checkIfAvatarIsUploaded();
                    } else {
                        sweetAlert.showErrorAlert("Error", "Email or password mismatch", "Retry");
                    }
                });
    }

    private void redirectToOtherPage(Activity activity) {
        Intent intent = new Intent(LoginActivity.this, activity.getClass());
        startActivity(intent);
        finish();
    }

    private boolean validateForm() {
        boolean validated = true;

        if (loginBinding.emailAddress.getText().toString().trim().isEmpty()) {
            loginBinding.emailAddress.setError("Please enter email address");
            sweetAlert.showErrorAlert("Error", "Please enter email address", "Ok");
            return false;
        }

        if (loginBinding.password.getText().toString().trim().isEmpty()) {
            loginBinding.password.setError("Please enter password");
            sweetAlert.showErrorAlert("Error", "Please enter password", "Ok");
        }

        return validated;
    }
}