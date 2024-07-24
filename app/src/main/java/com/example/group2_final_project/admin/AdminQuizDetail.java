package com.example.group2_final_project.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.group2_final_project.R;
import com.example.group2_final_project.admin.models.Quiz;
import com.example.group2_final_project.admin.models.QuizQuestion;
import com.example.group2_final_project.databinding.ActivityAdminQuizDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminQuizDetail extends AppCompatActivity {

    ActivityAdminQuizDetailBinding quizDetailBinding;
    private String quizId;
    Quiz quizDetail;
    private DatabaseReference quizRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quizDetailBinding =ActivityAdminQuizDetailBinding.inflate(getLayoutInflater());
        View view = quizDetailBinding.getRoot();
        setContentView(view);

        quizId = getIntent().getStringExtra("quizId");

        // Initialize Firebase database reference
        quizRef = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(quizId);
        fetchQuizDetails();
        setListeners();
    }

    private void setListeners() {
        quizDetailBinding.backButton.setOnClickListener(view -> finish());
        quizDetailBinding.questionsCard.setOnClickListener(view -> goToQuizQuestions());
    }

    private void goToQuizQuestions() {
        Intent quizQuestionsIntent = new Intent(this, AdminQuizQuestions.class);
        quizQuestionsIntent.putExtra("quizDetail", quizDetail);
        startActivity(quizQuestionsIntent);
    }

    private void fetchQuizDetails() {
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    quizDetail = snapshot.getValue(Quiz.class);
                    Glide.with(getApplicationContext()).load(quizDetail.getThumbnail())
                            .into(quizDetailBinding.quizThumbnail);

                    quizDetailBinding.quizDescription.setText("lorem test asdfn publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document or a typeface without relying on meaningful content. Lorem ipsum");
                    quizDetailBinding.questionsCount.setText(snapshot.child("questions").getChildrenCount()+" Questions");
                    quizDetailBinding.rewardPoints.setText(snapshot.child("questions").getChildrenCount() * 10 +" Points");
                } else {
                    // Handle the case where the quiz doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}