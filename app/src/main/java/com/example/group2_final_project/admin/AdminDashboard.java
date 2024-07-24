package com.example.group2_final_project.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.example.group2_final_project.R;
import com.example.group2_final_project.admin.adapters.QuizAdapter;
import com.example.group2_final_project.admin.models.Quiz;
import com.example.group2_final_project.admin.models.QuizQuestion;
import com.example.group2_final_project.auth.LoginActivity;
import com.example.group2_final_project.databinding.ActivityAdminDashboardBinding;
import com.example.group2_final_project.databinding.QuizAddPopupBinding;
import com.example.group2_final_project.helpers.SweetAlert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AdminDashboard extends AppCompatActivity {

    ActivityAdminDashboardBinding dashboardBinding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private DatabaseReference quizzesRef;
    private List<Quiz> quizList = new ArrayList<>();
    private QuizAdapter quizAdapter;
    private SweetAlert sweetAlert;
    Bitmap quizUploadImage;
    FirebaseUser loggedInUser;
    FirebaseStorage firebaseStorage;

    QuizAddPopupBinding quizAddPopupBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardBinding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        quizAddPopupBinding = QuizAddPopupBinding.inflate(getLayoutInflater());

        View view = dashboardBinding.getRoot();
        setContentView(view);

        sweetAlert = new SweetAlert(this);
        //SETTING UP FIREBASE
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        loggedInUser = firebaseAuth.getCurrentUser();

        firebaseStorage = FirebaseStorage.getInstance();

        // Initialize RecyclerView
        RecyclerView recyclerView = dashboardBinding.recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Initialize Firebase Realtime Database referencex
        quizzesRef = FirebaseDatabase.getInstance().getReference().child("Quizzes");

        quizAdapter = new QuizAdapter(this, quizList);
        recyclerView.setAdapter(quizAdapter);
        // Fetch quiz data from Firebase Realtime Database
        fetchQuizData();
        setListeners();
    }

    private void setListeners() {
        dashboardBinding.addQuizButton.setOnClickListener(view -> showAddQuizPopup(new Quiz()));
        dashboardBinding.logOut.setOnClickListener(view -> logOutOuser());
    }

    private void logOutOuser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //LIST QUIZ ON DASHBOARD
    private void fetchQuizData() {
        quizzesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Quiz quiz = dataSnapshot.getValue(Quiz.class);
                    if (quiz != null) {
                        List<QuizQuestion> quizQuestions = null;
                        for (DataSnapshot questionSnapshot : dataSnapshot.child("questions").getChildren()) {
                            QuizQuestion quizQuestion = questionSnapshot.getValue(QuizQuestion.class);
                            if (quizQuestion != null) {
                                quiz.getQuizQuestions().add(quizQuestion);
                            }
                        }
                        quizList.add(quiz);
                    }
                }

                quizAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //QUIZ ADD STARTS HERE
    private void showAddQuizPopup(Quiz quiz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.RoundedAlertDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.quiz_add_popup, null);

        int width = getResources().getDimensionPixelSize(R.dimen.dialog_width); // Define your desired width in resources
        dialogView.setMinimumWidth(width);

        if (quizAddPopupBinding.getRoot().getParent() != null)
            ((ViewGroup) quizAddPopupBinding.getRoot().getParent()).removeView(quizAddPopupBinding.getRoot());
        builder.setView(quizAddPopupBinding.getRoot());

        quizAddPopupBinding.quizAddImageView.setOnClickListener(view -> pickImage());
        AlertDialog dialog = builder.create();

        if (quiz.getId() == null) {
            dialog.setTitle("Add new Quiz");
        } else {
            quizAddPopupBinding.title.setText(quiz.getTitle());
            quizAddPopupBinding.description.setText(quiz.getDescription());
            Glide.with(this)
                    .load(quiz.getThumbnail())
                    .into(quizAddPopupBinding.quizAddImageView);
            dialog.setTitle("Edit Quiz Detail");
        }
        quizAddPopupBinding.submitQuizButton.setOnClickListener(view -> submitQuiz(dialog, quiz));
        dialog.show();
    }

    private void submitQuiz(AlertDialog dialog, Quiz quiz) {
        if (validateForm()) {
            dialog.hide();
            initiateQuizSubmission(quiz);
        }
    }

    // Submit quiz details to Firebase
    private void initiateQuizSubmission(Quiz quizDetail) {
        sweetAlert.showLoader("Saving Quiz Details");
        if (quizUploadImage instanceof Bitmap) {
            String uid = loggedInUser.getUid();
            Random random = new Random();
            long randomNumber = random.nextLong() % 10000000000L + 1;
            StorageReference storageRef = firebaseStorage.getReference().child("quiz_thumbnails").child(randomNumber + "");

            Bitmap bitmap = (Bitmap) quizUploadImage;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            storageRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            if (quizDetail.getId() == null) {
                                String quizID = quizzesRef.push().getKey();
                                Quiz quiz = new Quiz(
                                        quizID,
                                        quizAddPopupBinding.title.getText().toString().trim(),
                                        quizAddPopupBinding.description.getText().toString().trim(),
                                        uri.toString(),
                                        "0"
                                );
                                quizzesRef.child(quizID).setValue(quiz);
                                sweetAlert.hideLoader();
                            } else {
                                updateQuizDetail(quizDetail, uri.toString());
                            }
                        }).addOnFailureListener(e -> {
                            Log.e("FirebaseStorage", "Error getting download URL: " + e.getMessage(), e);
                        });
                    }).addOnFailureListener(e -> {
                        // Handle failure
                        Log.e("FirebaseStorage", "Error uploading image: " + e.getMessage(), e);
                    });
        } else {
            updateQuizDetail(quizDetail, quizDetail.getThumbnail());
        }
    }

    protected void updateQuizDetail(Quiz quizDetail, String imageUrl) {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("title", quizAddPopupBinding.title.getText().toString().trim());
        updateMap.put("description", quizAddPopupBinding.description.getText().toString().trim());
        updateMap.put("thumbnail", imageUrl);
        quizzesRef.child(quizDetail.getId()).updateChildren(updateMap);
        sweetAlert.hideLoader();
    }

    public void showAddEditQuizDialog(Quiz quizDetail) {
        showAddQuizPopup(quizDetail);
    }

    private boolean validateForm() {
        boolean validated = true;

        if (quizAddPopupBinding.title.getText().toString().trim().isEmpty()) {
            quizAddPopupBinding.title.setError("Please enter quiz title.");
        }

        if (quizUploadImage == null) {
            quizAddPopupBinding.quizAddImageView.setBackgroundColor(getResources().getColor(R.color.light_red));
        }

        if (quizAddPopupBinding.description.getText().toString().trim().isEmpty()) {
            quizAddPopupBinding.description.setError("Please enter quiz description");
        }


        return validated;
    }

    // Image picking logic
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                quizAddPopupBinding.quizAddImageView.setImageBitmap(bitmap);
                quizAddPopupBinding.quizAddImageView.setBackground(getResources().getDrawable(R.drawable.dashed_border));
                quizUploadImage = bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}