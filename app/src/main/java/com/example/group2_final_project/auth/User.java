package com.example.group2_final_project.auth;

public class User {
    private String full_name;
    private  String avatar;

    public User() {
        this.full_name = "";
        this.avatar = "";
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String full_name) {
        this.full_name = full_name;
        this.avatar = "";
    }

    public User(String full_name, String avatar) {
        this.full_name = full_name;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }



    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
}
