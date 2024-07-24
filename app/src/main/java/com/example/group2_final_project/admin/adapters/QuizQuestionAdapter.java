package com.example.group2_final_project.admin.adapters;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.example.group2_final_project.R;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group2_final_project.admin.models.Quiz;
import com.example.group2_final_project.admin.models.QuizQuestion;
import com.example.group2_final_project.helpers.SweetAlert;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuizQuestionAdapter extends RecyclerView.Adapter<QuizQuestionAdapter.QuizQuestionViewHolder> {

    private List<QuizQuestion> quizQuestions;
    Context parentContext;

    String quizID;
    public QuizQuestionAdapter(Context parentContext, List<QuizQuestion> quizQuestions, String quizID) {
        this.quizQuestions = quizQuestions;
        this.parentContext = parentContext;
        this.quizID = quizID;
    }

    @NonNull
    @Override
    public QuizQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_quiz_question_list_item, parent, false);
        return new QuizQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizQuestionViewHolder holder, int position) {
        QuizQuestion quizQuestion = quizQuestions.get(position);
        holder.questionText.setText(quizQuestion.getQuestion());

        holder.optionsGridLayout.removeAllViews();
        for (int i = 0; i < quizQuestion.getOptions().size(); i++) {
            LinearLayout linearLayout = new LinearLayout(holder.itemView.getContext());

            TextView textView = new TextView(holder.itemView.getContext());
            textView.setText(i+1+". "+quizQuestion.getOptions().get(i));
            textView.setTextSize(18);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f));
            params.width = 0;
            params.topMargin = 25;
            linearLayout.setLayoutParams(params);

            // Check the correct answer
            if (quizQuestion.getCorrect_answer().equals(quizQuestion.getOptions().get(i))) {
                textView.setPadding(20,10,20,10);
                textView.setBackgroundResource(R.drawable.badge_background);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
            }
            linearLayout.addView(textView);
            holder.optionsGridLayout.addView(linearLayout);
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialog alertDialog = new SweetAlertDialog(parentContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this question!")
                        .setConfirmText("Yes,delete it!")
                        .showCancelButton(true)
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                int adapterPosition = holder.getAdapterPosition();
                                if (adapterPosition != RecyclerView.NO_POSITION) {
                                    QuizQuestion quizQuestion = quizQuestions.get(adapterPosition);
                                    deleteQuizQuestionFromFirebase(quizQuestion.getId());
                                }
                                sDialog.dismissWithAnimation();
                            }
                        });

                alertDialog.show();
                alertDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(parentContext, R.color.sweet_red)));
            }
        });
    }

    private void deleteQuizQuestionFromFirebase(String questionID) {
        DatabaseReference questionReff = FirebaseDatabase.getInstance().getReference("Quizzes").child(quizID).child("questions").child(questionID);
        questionReff.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SweetAlert sweetAlert = new SweetAlert(parentContext);
                sweetAlert.showSuccessAlert("Success", "Quiz question successfully deleted", "Ok");
            }
        });
    }

    // Returns the total number of items in the data set held by the adapter
    @Override
    public int getItemCount() {
        return quizQuestions.size();
    }
    // ViewHolder class to hold and recycle views as they are scrolled screen
    public class QuizQuestionViewHolder extends RecyclerView.ViewHolder {
        private TextView questionText;
        private GridLayout optionsGridLayout;
        private ImageView deleteButton;
        public QuizQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.text_question);
            optionsGridLayout = itemView.findViewById(R.id.grid_layout_options);
            deleteButton = itemView.findViewById(R.id.buttonDeleteQuizQuestion);
        }
    }
}