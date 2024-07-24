package com.example.group2_final_project.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.group2_final_project.R;
import com.example.group2_final_project.admin.models.Quiz;
import com.example.group2_final_project.admin.models.QuizQuestion;
import com.example.group2_final_project.databinding.ActivityQuizPlayerBinding;
import com.example.group2_final_project.helpers.SweetAlert;
import com.example.group2_final_project.user.adapters.QuizPlayerAdapter;
import com.example.group2_final_project.user.models.QuizAttempt;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class QuizPlayer extends AppCompatActivity {

    private Quiz quizDetail;
    private ActivityQuizPlayerBinding quizPlayerBinding;
    private ViewPager2 quizQuestionPager;
    private QuizPlayerAdapter quizPlayerAdapter;
    private FirebaseUser loggedInUser;
    private int currentQuizQuestionPosition;
    private SweetAlert sweetAlert;
    private DatabaseReference attemptsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quizPlayerBinding = ActivityQuizPlayerBinding.inflate(getLayoutInflater());
        View view = quizPlayerBinding.getRoot();
        setContentView(view);
        currentQuizQuestionPosition = 0;

        sweetAlert = new SweetAlert(this);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        loggedInUser = firebaseAuth.getCurrentUser();

        attemptsRef = FirebaseDatabase.getInstance().getReference().child("Attempts");

        Intent intent = getIntent();
        quizDetail = (Quiz) intent.getSerializableExtra("quizDetail");
        setPageData();
        setUpQuestionPager();
    }

    private void setUpQuestionPager() {
        quizQuestionPager = findViewById(R.id.quizQuestionsHolder);
        quizPlayerAdapter = new QuizPlayerAdapter(quizDetail.getQuizQuestions());
        quizQuestionPager.setAdapter(quizPlayerAdapter);

        quizQuestionPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentQuizQuestionPosition = position;
                quizPlayerBinding.progressBar.setText("Question " + (position+1) +" out of " + quizDetail.getQuizQuestions().size());
                quizPlayerAdapter.notifyDataSetChanged();

                if ((position+1) == quizDetail.getQuizQuestions().size()) {
                    quizPlayerBinding.submitQuizButton.setVisibility(View.VISIBLE);
                    quizPlayerBinding.nextButton.setVisibility(View.GONE);
                } else {
                    quizPlayerBinding.submitQuizButton.setVisibility(View.GONE);
                    quizPlayerBinding.nextButton.setVisibility(View.VISIBLE);

                }
            }
        });

        quizPlayerBinding.nextButton.setOnClickListener(v -> {
            if (currentQuizQuestionPosition < quizDetail.getQuizQuestions().size()) {
                quizQuestionPager.setCurrentItem(currentQuizQuestionPosition + 1);
            }
        });

        quizPlayerBinding.previousButton.setOnClickListener(v -> {
            if (currentQuizQuestionPosition > 0) {
                quizQuestionPager.setCurrentItem(currentQuizQuestionPosition-1);
            }
        });

        quizPlayerBinding.submitQuizButton.setOnClickListener(v -> submitQuiz());
    }

    private void submitQuiz() {
        // Calculate user's score and validate if all questions are answered
        List<QuizQuestion> quizQuestions = quizPlayerAdapter.getQuizQuestions();
        int correctAnswers = 0;
        int userAnswers = 0;
        for (QuizQuestion quizQuestion : quizQuestions) {
            if (quizQuestion.getUser_answer() != null) {
                if (quizQuestion.getUser_answer().equals(quizQuestion.getCorrect_answer())) {
                    correctAnswers++;
                }
                userAnswers++;
            }
        }
        if (userAnswers != quizQuestions.size()) {
            sweetAlert.showErrorAlert("Error", "Please answer all questions", "OK");
            return;
        }

        sweetAlert.showLoader("Submitting Quiz.. Hold on!!");
        String key = attemptsRef.push().getKey();
        QuizAttempt quizAttempts = new QuizAttempt(key, loggedInUser.getUid(), quizQuestions.size(), correctAnswers, quizQuestions.size() - correctAnswers);

        attemptsRef.child(key).setValue(quizAttempts).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(this, QuizResult.class);
                intent.putExtra("quizAttempt", quizAttempts);
                startActivity(intent);
                finish();
            } else {
                sweetAlert.showErrorAlert("Error", "Something went wrong", "OK");
            }
        });


        //


//        if (userAnswers == quizQuestions.size()) {
//            Log.i("QUIZ", "Correct Answers: " + correctAnswers);
//            Log.i("QUIZ", "User Answers: " + userAnswers);
//            Log.i("QUIZ", "Quiz Questions: " + quizQuestions.size());
//            Log.i("QUIZ", "Quiz Score: " + (correctAnswers * 100) / quizQuestions.size());
//            Intent intent = new Intent(this, QuizResult.class);
//            intent.putExtra("quizScore", (correctAnswers * 100) / quizQuestions.size());
//            startActivity(intent);
//        } else {
//            Log.i("QUIZ", "User Answers: " + userAnswers);
//            Log.i("QUIZ", "Quiz Questions: " + quizQuestions.size());
//            Log.i("QUIZ", "Quiz Score: " + (correctAnswers * 100) / quizQuestions.size());
//            Intent intent = new Intent(this, QuizResult.class);
//            intent.putExtra("quizScore", (correctAnswers * 100) / quizQuestions.size());
//            startActivity(intent);
//        }
    }
    // Method to set the quiz title in the UI
    private void setPageData() {
        quizPlayerBinding.quizTitle.setText(quizDetail.getTitle());
    }
}