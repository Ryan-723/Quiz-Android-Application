package com.example.group2_final_project.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.group2_final_project.R;
import com.example.group2_final_project.admin.adapters.QuizQuestionAdapter;
import com.example.group2_final_project.admin.models.Quiz;
import com.example.group2_final_project.admin.models.QuizQuestion;
import com.example.group2_final_project.databinding.ActivityAdminQuizQuestionsBinding;
import com.example.group2_final_project.databinding.QuizQuestionAddPopupBinding;
import com.example.group2_final_project.helpers.SweetAlert;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminQuizQuestions extends AppCompatActivity {

    ActivityAdminQuizQuestionsBinding quizQuestionsBinding;
    QuizQuestionAddPopupBinding quizQuestionAddPopupBinding;
    private DatabaseReference databaseReference;
    Quiz quizDetail;
    private static final String QUIZZES_NODE = "Quizzes";
    private List<QuizQuestion> questionList = new ArrayList<>();
    SweetAlert sweetAlert;
    private RadioButton correctAnswerRadio;
    private QuizQuestionAdapter questionAdapter;
    List<LinearLayout> optionLayouts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quizQuestionsBinding = ActivityAdminQuizQuestionsBinding.inflate(getLayoutInflater());
        quizQuestionAddPopupBinding = QuizQuestionAddPopupBinding.inflate(getLayoutInflater());
        setContentView(quizQuestionsBinding.getRoot());

        sweetAlert = new SweetAlert(this);

        quizDetail = (Quiz) getIntent().getSerializableExtra("quizDetail");
        quizQuestionsBinding.quizTitle.setText(quizDetail.getTitle());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Initialize RecyclerView
        RecyclerView recyclerView = quizQuestionsBinding.quizQuestionsHolder;
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        questionAdapter = new QuizQuestionAdapter(this, questionList, quizDetail.getId());
        recyclerView.setAdapter(questionAdapter);
        fetchDataFromFirebase();
        setListeners();
    }

    private void fetchDataFromFirebase() {
        // Fetching quiz questions from Firebase and updating the RecyclerView
        databaseReference.child("Quizzes").child(quizDetail.getId()).child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionList.clear();
                for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                    QuizQuestion question = questionSnapshot.getValue(QuizQuestion.class);
                    questionList.add(question);
                }

                if (questionList.size() == 0) {
                    quizQuestionsBinding.quizQuestionsHolder.setVisibility(View.GONE);
                    quizQuestionsBinding.viewNoQuestion.setVisibility(View.VISIBLE);
                } else {
                    quizQuestionsBinding.quizQuestionsHolder.setVisibility(View.VISIBLE);
                    quizQuestionsBinding.viewNoQuestion.setVisibility(View.GONE);
                }
                questionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setListeners() {
        quizQuestionsBinding.backButton.setOnClickListener(view -> finish());
        quizQuestionsBinding.addQuizQuestion.setOnClickListener(view -> showAddQuestionPopup());
    }

    private void showAddQuestionPopup() {
        // Create an AlertDialog to add a new question
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (quizQuestionAddPopupBinding.getRoot().getParent() != null)
            ((ViewGroup) quizQuestionAddPopupBinding.getRoot().getParent()).removeView(quizQuestionAddPopupBinding.getRoot());
        builder.setView(quizQuestionAddPopupBinding.getRoot());


        quizQuestionAddPopupBinding.editTextTotalOptions.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!quizQuestionAddPopupBinding.editTextTotalOptions.getText().toString().trim().isEmpty()) {
                    int totalOptions = Integer.parseInt(quizQuestionAddPopupBinding.editTextTotalOptions.getText().toString());
                    if (totalOptions > 0 && totalOptions <= 6) {
                        optionLayouts = setupDynamicOptionFields(quizQuestionAddPopupBinding.optionsContainer, totalOptions);
                    } else if (totalOptions > 6) {
                        sweetAlert.showErrorAlert("Error", "You can only set 6 options at max", "Got It !");
                    }
                }
            }
        });
        // Set up dynamic option fields based on total options
        int totalOptions = Integer.parseInt(quizQuestionAddPopupBinding.editTextTotalOptions.getText().toString());
         optionLayouts = setupDynamicOptionFields(quizQuestionAddPopupBinding.optionsContainer, totalOptions);
        AlertDialog dialog = builder.create();

        quizQuestionAddPopupBinding.saveQuizQuestion.setOnClickListener(view -> initiateQuestionSave(dialog));
        dialog.setTitle("Setup Question");
        dialog.show();
    }

    private void initiateQuestionSave(AlertDialog dialog) {
        String questionText = quizQuestionAddPopupBinding.editTextQuestion.getText().toString().trim();
        if (questionText.isEmpty()) {
            sweetAlert.showErrorAlert("Error", "Question cannot be blank!", "Got it!");
            return;
        }
        List<String> options = new ArrayList<>();
        int numberOfOptions = Integer.parseInt(quizQuestionAddPopupBinding.editTextTotalOptions.getText().toString());
        int correctAnswerIndex = -1;
        for (int layoutIndex = 0; layoutIndex < numberOfOptions; layoutIndex++) {
            LinearLayout optionLayout = optionLayouts.get(layoutIndex);
            EditText optionEditText = (EditText) optionLayout.getChildAt(0);
            RadioButton radioButton = (RadioButton) optionLayout.getChildAt(1);
            String optionText = optionEditText.getText().toString().trim();
            if (optionText.isEmpty()) {
                sweetAlert.showErrorAlert("Error", "Option field shouldn't be left blank "+layoutIndex, "Got it!");
                return;
            }
            options.add(optionText);
            if (radioButton.isChecked()) {
                correctAnswerIndex = layoutIndex;
            }
        }
        if (correctAnswerIndex == -1) {
            sweetAlert.showErrorAlert("Error", "You must select a correct answer", "Got it!");
            return;
        }
        String questionKey = databaseReference.child(QUIZZES_NODE).child(quizDetail.getId()).child("questions").push().getKey();
        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("id", questionKey);
        questionMap.put("question", questionText);
        questionMap.put("options", options);
        questionMap.put("correct_answer", options.get(correctAnswerIndex));
        databaseReference.child(QUIZZES_NODE).child(quizDetail.getId()).child("questions").child(questionKey).setValue(questionMap);

        quizQuestionAddPopupBinding.editTextQuestion.setText("");
        quizQuestionAddPopupBinding.editTextTotalOptions.setText("4");
        dialog.hide();
    }

    private List<LinearLayout> setupDynamicOptionFields(LinearLayout optionsContainer, int totalOptions) {
        optionsContainer.removeAllViews(); // Clear existing options
        List<LinearLayout> optionLayouts = new ArrayList<>();

        for (int i = 1; i <= totalOptions; i++) {
            // Create a LinearLayout for each option
            LinearLayout optionLayout = new LinearLayout(this);
            optionLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            optionLayout.setOrientation(LinearLayout.HORIZONTAL);

            // Create an EditText for the option
            EditText optionEditText = new EditText(this);
            optionEditText.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1 // Weight to make it take remaining space
            ));

            optionEditText.setBackground(getResources().getDrawable(R.drawable.textfield_border));
            optionEditText.setHint("Option " + i);

            setMarginBottom(optionLayout, 20);
            optionEditText.setPaddingRelative(30, optionEditText.getPaddingTop(), optionEditText.getPaddingEnd(), optionEditText.getPaddingBottom());

            // Create a RadioButton for the option
            RadioButton radioButton = new RadioButton(this);
            radioButton.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            radioButton.setText("Correct");
            radioButton.setTag(optionEditText); // Associate the radio button with its corresponding option EditText

            optionLayout.addView(optionEditText);
            optionLayout.addView(radioButton);
            optionLayouts.add(optionLayout);

            // Add the option LinearLayout to the container
            optionsContainer.addView(optionLayout);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleRadioButtonClick(radioButton);
                }
            });
        }

        return optionLayouts;
    }

    private int getSelectedOptionIndex(RadioGroup radioGroup) {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        View checkedRadioButton = radioGroup.findViewById(checkedId);
        return radioGroup.indexOfChild(checkedRadioButton);
    }

    private void handleRadioButtonClick(RadioButton clickedRadioButton) {
        // Uncheck the previously selected RadioButton
        if (correctAnswerRadio != null && correctAnswerRadio != clickedRadioButton) {
            correctAnswerRadio.setChecked(false);
        }

        // Update the selected RadioButton
        correctAnswerRadio = clickedRadioButton;
    }

    private void setMarginBottom(View view, int marginBottom) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, marginBottom);
        view.setLayoutParams(params);
    }
}