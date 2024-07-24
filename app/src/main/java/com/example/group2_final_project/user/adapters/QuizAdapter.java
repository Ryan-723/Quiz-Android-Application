package com.example.group2_final_project.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.group2_final_project.R;
import com.example.group2_final_project.admin.AdminQuizDetail;
import com.example.group2_final_project.admin.models.Quiz;
import com.example.group2_final_project.user.QuizDetail;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<Quiz> quizList;
    Context parentContext;

    public QuizAdapter(Context parentContext, List<Quiz> quizList) {
        this.quizList = quizList;
        this.parentContext = parentContext;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_quiz_list_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);

        holder.titleTextView.setText(quiz.getTitle());
        holder.descriptionTextView.setText(quiz.getQuizQuestions().size() +" Questions");
        Glide.with(parentContext)
                .load(quiz.getThumbnail())
                .into(holder.quizThumbnail);

        holder.quizThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parentContext, QuizDetail.class);
                intent.putExtra("quizId", quiz.getId());
                parentContext.startActivity(intent);
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
        ImageView quizThumbnail;
        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            quizThumbnail = itemView.findViewById(R.id.quizThumbnail);
        }
    }
}
