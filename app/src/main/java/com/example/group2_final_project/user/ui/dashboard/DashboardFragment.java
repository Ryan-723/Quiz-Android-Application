package com.example.group2_final_project.user.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.group2_final_project.admin.models.Quiz;
import com.example.group2_final_project.admin.models.QuizQuestion;
import com.example.group2_final_project.databinding.FragmentDashboardBinding;
import com.example.group2_final_project.user.adapters.QuizAdapter;
import com.example.group2_final_project.user.models.QuizAttempt;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private DatabaseReference quizzesRef;
    private FirebaseUser firebaseUser;
    private List<Quiz> quizList = new ArrayList<>();
    private QuizAdapter quizAdapter;
    private FragmentDashboardBinding binding;
    private int doneQuizzes = 0;
    private int expPoints = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Creating the fragment binding
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Getting the loggedin user instance from firebase
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setUserData();

        // Initialize RecyclerView
        RecyclerView recyclerView = binding.quizRecyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        // Initialize Firebase Realtime Database referencex
        quizzesRef = FirebaseDatabase.getInstance().getReference().child("Quizzes");
        quizAdapter = new QuizAdapter(getActivity(), quizList);
        recyclerView.setAdapter(quizAdapter);

        fetchQuizData();
        fetchUserQuizAttempts();
        return root;
    }

    private void fetchUserQuizAttempts() {
        DatabaseReference userAttemptsRef = FirebaseDatabase.getInstance().getReference().child("Attempts");;
        userAttemptsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doneQuizzes = 0;
                expPoints = 0;
                for (DataSnapshot attemptSnapshot : snapshot.getChildren()) {
                    QuizAttempt quizAttempt = attemptSnapshot.getValue(QuizAttempt.class);
                    if (quizAttempt.getUser_id().equals(firebaseUser.getUid())) {
                        doneQuizzes++;
                        expPoints += quizAttempt.getCorrect_answers() * 10;
                    }
                }

                binding.userExpPoints.setText(String.valueOf(expPoints));
                binding.doneQuizzes.setText(String.valueOf(doneQuizzes));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Method to set user's Name and profile picture
     */
    private void setUserData() {
        binding.userWelcomeName.setText("Hey " + firebaseUser.getDisplayName());
        Glide.with(getActivity())
                .load(firebaseUser.getPhotoUrl())
                .into(binding.userProfileImage);
    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}