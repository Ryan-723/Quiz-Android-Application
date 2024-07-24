package com.example.group2_final_project.user.models;

import java.io.Serializable;

public class QuizAttempt implements Serializable {
    String id;
    String user_id;
    int total_questions;
    int correct_answers;
    int wrong_answers;

    public QuizAttempt() {
    }

    public QuizAttempt(String id, String user_id, int total_questions, int correct_answers, int wrong_answers) {
        this.id = id;
        this.user_id = user_id;
        this.total_questions = total_questions;
        this.correct_answers = correct_answers;
        this.wrong_answers = wrong_answers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getTotal_questions() {
        return total_questions;
    }

    public void setTotal_questions(int total_questions) {
        this.total_questions = total_questions;
    }

    public int getCorrect_answers() {
        return correct_answers;
    }

    public void setCorrect_answers(int correct_answers) {
        this.correct_answers = correct_answers;
    }

    public int getWrong_answers() {
        return wrong_answers;
    }

    public void setWrong_answers(int wrong_answers) {
        this.wrong_answers = wrong_answers;
    }
}
