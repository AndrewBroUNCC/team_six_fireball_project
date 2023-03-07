package com.example.team_six_fireball_project;

public class User {
    String email, joinDate, name, uri, userID;



    public User(String email, String joinDate, String name, String uri, String userID) {
        this.email =email;
        this.joinDate = joinDate;
        this.name = name;
        this.uri = uri;
        this.userID = userID;
    }

    public User() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", joinDate='" + joinDate + '\'' +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}