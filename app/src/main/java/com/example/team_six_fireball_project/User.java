package com.example.team_six_fireball_project;

public class User {
    String name, userID;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", userID='" + userID + '\'' +
                '}';
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

    public User() {
    }

    public User(String name, String userID) {
        this.name = name;
        this.userID = userID;
    }
}