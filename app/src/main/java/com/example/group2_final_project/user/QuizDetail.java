package com.example.group2_final_project.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.group2_final_project.R;
import com.example.group2_final_project.admin.AdminQuizQuestions;
import com.example.group2_final_project.admin.models.Quiz;
import com.example.group2_final_project.admin.models.QuizQuestion;
import com.example.group2_final_project.databinding.ActivityAdminQuizDetailBinding;
import com.example.group2_final_project.databinding.ActivityQuizDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizDetail extends AppCompatActivity {

    private ActivityQuizDetailBinding quizDetailBinding;
    private String quizId;
    private Quiz quizDetail;
    private DatabaseReference quizRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quizDetailBinding = ActivityQuizDetailBinding.inflate(getLayoutInflater());
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
        quizDetailBinding.btnStartQuiz.setOnClickListener(view -> {
            // Start QuizPlayer activity and pass quizDetail
            Intent quizPlayerIntent = new Intent(this, QuizPlayer.class);
            quizPlayerIntent.putExtra("quizDetail", quizDetail);
            startActivity(quizPlayerIntent);
        });
    }

    private void fetchQuizDetails() {
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get quiz details from Firebase snapshot
                    quizDetail = snapshot.getValue(Quiz.class);
                    Glide.with(getApplicationContext()).load(quizDetail.getThumbnail())
                            .into(quizDetailBinding.quizThumbnail);

                    List<QuizQuestion> quizQuestions = new ArrayList<>();
                    for (DataSnapshot questionSnapshot : snapshot.child("questions").getChildren()) {
                        QuizQuestion question = questionSnapshot.getValue(QuizQuestion.class);
                        quizQuestions.add(question);
                    }
                    // Set quiz questions to the fetched quiz details
                    quizDetail.setQuizQuestions(quizQuestions);
                    // Update UI elements with quiz details
                    quizDetailBinding.quizTitle.setText(quizDetail.getTitle());
                    quizDetailBinding.quizDescription.setText(quizDetail.getDescription() + "\n\nLorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.");
                    quizDetailBinding.questionsCount.setText(snapshot.child("questions").getChildrenCount()+" Questions");
                    quizDetailBinding.rewardPoints.setText(snapshot.child("questions").getChildrenCount() * 10 +" Points");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}