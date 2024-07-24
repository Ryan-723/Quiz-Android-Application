package com.example.group2_final_project.user.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group2_final_project.R;
import com.example.group2_final_project.admin.models.Quiz;
import com.example.group2_final_project.admin.models.QuizQuestion;
import com.example.group2_final_project.onboarding.OnboardingScreenItem;
import com.example.group2_final_project.onboarding.OnboardingViewPagerAdapter;

import java.util.List;

public class QuizPlayerAdapter extends RecyclerView.Adapter<QuizPlayerAdapter.ViewHolder>  {

    List<QuizQuestion> quizQuestions;

    public QuizPlayerAdapter(List<QuizQuestion> quizQuestions) {
        this.quizQuestions = quizQuestions;
    }

    public List<QuizQuestion> getQuizQuestions() {
        return quizQuestions;
    }

    @NonNull
    @Override
    public QuizPlayerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_player_question, parent, false);
        return new QuizPlayerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizPlayerAdapter.ViewHolder holder, int position) {
        QuizQuestion quizQuestion = quizQuestions.get(holder.getAdapterPosition());

        holder.quizQuestion.setText(quizQuestion.getQuestion());
        holder.bind(quizQuestion);
    }

    @Override
    public int getItemCount() {
        return quizQuestions.size();
    }

    public void setSelectedAnswer(int currentPosition, String answer) {
        if (currentPosition < 0 || currentPosition > quizQuestions.size()) {
            currentPosition = 0;
        }
        QuizQuestion quizQuestion = quizQuestions.get(currentPosition);
        quizQuestion.setUser_answer(answer);
    }


//    @Override
//    public void onOptionClick(String selectedOption) {
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView quizQuestion;
        private RecyclerView optionsRecyclerView;
        private OptionsAdapter optionsAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.quizQuestion = itemView.findViewById(R.id.quizQuestion);

            optionsRecyclerView = itemView.findViewById(R.id.quizOptionsHolder);
            optionsRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 2));
            optionsAdapter = new OptionsAdapter();
            optionsRecyclerView.setAdapter(optionsAdapter);

            optionsAdapter.setOptionClickListener(selectedOption -> {
                setSelectedAnswer(getAdapterPosition(), selectedOption);
            });
        }

        public void bind(QuizQuestion quizQuestion) {

            optionsAdapter.setOptions(quizQuestion.getOptions());

            if (quizQuestion.getUser_answer() != null) {
                Log.i("USERANS", quizQuestion.getUser_answer());
                optionsAdapter.setSelectedItemPosition(quizQuestion.getUser_answer());
            }
        }
    }
}

class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.OptionViewHolder> {
    public interface OptionClickListener {
        void onOptionClick(String selectedOption);
    }

    private String selectedItemOption;
    private OptionClickListener optionClickListener;

    public void setOptionClickListener(OptionClickListener listener) {
        this.optionClickListener = listener;
    }

    public void setSelectedItemPosition(String option) {
        selectedItemOption = option;
        notifyDataSetChanged();
    }

    private List<String> options;
    public void setOptions(List<String> options) {
        this.options = options;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OptionsAdapter.OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_player_option, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsAdapter.OptionViewHolder holder, int position) {
        String option = options.get(position);
        holder.bind(option, option == selectedItemOption);
    }

    @Override
    public int getItemCount() {
        return options != null ? options.size() : 0;
    }

    public class OptionViewHolder extends RecyclerView.ViewHolder {
        private TextView optionText;
        private String option;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            optionText = itemView.findViewById(R.id.quizQuestionOption);
            itemView.setOnClickListener(v -> {
                if (optionClickListener != null) {
                    optionClickListener.onOptionClick(option);
                    setSelectedItemPosition(option);
                }
            });
        }

        public void bind(String option, boolean isSelected) {
            this.option = option;
            optionText.setText(option);

            if (isSelected) {
                itemView.findViewById(R.id.optionLayout).setBackgroundColor(itemView.getContext().getResources().getColor(R.color.my_light_primary));
                optionText.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } else {
                itemView.findViewById(R.id.optionLayout).setBackgroundColor(itemView.getContext().getResources().getColor(R.color.white));
                optionText.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
            }
        }
    }
}
