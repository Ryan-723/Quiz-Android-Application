package com.example.group2_final_project.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.group2_final_project.R;
import com.example.group2_final_project.databinding.ActivityQuizResultBinding;
import com.example.group2_final_project.user.models.QuizAttempt;

public class QuizResult extends AppCompatActivity {
    private ActivityQuizResultBinding quizResultBinding;
    private QuizAttempt quizAttempt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quizResultBinding = ActivityQuizResultBinding.inflate(getLayoutInflater());
        View view = quizResultBinding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        quizAttempt = (QuizAttempt) intent.getSerializableExtra("quizAttempt");

        quizResultBinding.backButton.setOnClickListener(v -> finish());

        setPageData();
    }

    private void setPageData() {
        quizResultBinding.txtScore.setText(quizAttempt.getCorrect_answers() + " / " + quizAttempt.getTotal_questions());
        quizResultBinding.rewardPoints.setText(quizAttempt.getCorrect_answers() * 10 + " Points");
    }
}