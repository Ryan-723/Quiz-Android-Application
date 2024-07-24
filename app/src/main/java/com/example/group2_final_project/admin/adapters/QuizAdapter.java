package com.example.group2_final_project.admin.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.group2_final_project.R;
import com.example.group2_final_project.admin.AdminDashboard;
import com.example.group2_final_project.admin.AdminQuizDetail;
import com.example.group2_final_project.admin.models.Quiz;
import com.example.group2_final_project.helpers.SweetAlert;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<Quiz> quizList;
    Context parentContext;

    // Constructor initializing the adapter with data and parent context
    public QuizAdapter(Context parentContext, List<Quiz> quizList) {
        this.quizList = quizList;
        this.parentContext = parentContext;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_quiz_list_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);

        // Setting quiz title, description, and thumbnail using Glide
        holder.titleTextView.setText(quiz.getTitle());
        holder.descriptionTextView.setText(quiz.getQuizQuestions().size() +" Questions");
        Glide.with(parentContext)
                .load(quiz.getThumbnail())
                .into(holder.quizThumbnail);
        holder.buttonDeleteQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirmation dialog for quiz deletion
                SweetAlertDialog alertDialog = new SweetAlertDialog(parentContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this quiz detail!")
                        .setConfirmText("Yes,delete it!")
                        .showCancelButton(true)
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                int adapterPosition = holder.getAdapterPosition();
                                if (adapterPosition != RecyclerView.NO_POSITION) {
                                    Quiz quiz = quizList.get(adapterPosition);
                                    deleteQuizFromFirebase(quiz.getId());
                                }
                                sDialog.dismissWithAnimation();
                            }
                        });

                alertDialog.show();
                alertDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(parentContext, R.color.sweet_red)));

            }
        });

        holder.buttonEditQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AdminDashboard) parentContext).showAddEditQuizDialog(quiz);
            }
        });

        holder.quizThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parentContext, AdminQuizDetail.class);
                intent.putExtra("quizId", quiz.getId());
                parentContext.startActivity(intent);
            }
        });
    }

    private void deleteQuizFromFirebase(String quizId) {
        DatabaseReference quizzesRef = FirebaseDatabase.getInstance().getReference("Quizzes").child(quizId);
        quizzesRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SweetAlert sweetAlert = new SweetAlert(parentContext);
                sweetAlert.showSuccessAlert("Success", "Quiz successfully deleted", "Ok");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure, e.g., show an error message
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView buttonDeleteQuiz;
        ImageView quizThumbnail;

        ImageView buttonEditQuiz;
        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            buttonDeleteQuiz = itemView.findViewById(R.id.buttonDeleteQuiz);
            quizThumbnail = itemView.findViewById(R.id.quizThumbnail);
            buttonEditQuiz = itemView.findViewById(R.id.buttonEditQuiz);
        }
    }
}
