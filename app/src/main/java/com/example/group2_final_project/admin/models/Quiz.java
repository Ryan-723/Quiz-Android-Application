package com.example.group2_final_project.admin.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Quiz implements Serializable {
    private  String id;
    private String title;

    private  String description;

    private String thumbnail;

    private String isPublished;

    private List<QuizQuestion> questions = new ArrayList<>();;
    public Quiz() {

    }
    public Quiz(String id, String title, String description, String thumbnail, String isPublished) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.isPublished = isPublished;
    }

    public Quiz(String id, String title, String description, String thumbnail, String isPublished, List<QuizQuestion> questions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.isPublished = isPublished;
        this.questions = questions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(String isPublished) {
        this.isPublished = isPublished;
    }

    public List<QuizQuestion> getQuizQuestions() {
        return questions;
    }

    public void setQuizQuestions(List<QuizQuestion> quizQuestions) {
        this.questions = quizQuestions;
    }
}
