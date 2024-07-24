package com.example.group2_final_project.admin.models;

import java.io.Serializable;
import java.util.List;

public class QuizQuestion implements Serializable {
    String id;
    List<String> options;
    String question;
    String correct_answer;

    String user_answer;

    public String getUser_answer() {
        return user_answer;
    }

    public void setUser_answer(String user_answer) {
        this.user_answer = user_answer;
    }

    public QuizQuestion() {
    }

    public QuizQuestion(String id, List<String> options, String question, String correct_answer) {
        this.id = id;
        this.options = options;
        this.question = question;
        this.correct_answer = correct_answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }
}
