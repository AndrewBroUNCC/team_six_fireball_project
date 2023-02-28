package com.example.team_six_fireball_project;

public class Forum {
    //Forum title, name of forum creator, the forum description, the date/time the forum
    //was created.
    String title;
    String creator;
    String description;
    String createdDate;
    String forumID;
    String userID;

    public String getForumID() {
        return forumID;
    }

    public void setForumID(String forumID) {
        this.forumID = forumID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Forum(String title, String creator, String description, String createdDate, String userID, String forumID) {
        this.title = title;
        this.creator = creator;
        this.description = description;
        this.createdDate = createdDate;
        this.userID = userID;
        this.forumID = forumID;

    }

    public Forum() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Forum{" +
                "title='" + title + '\'' +
                ", creator='" + creator + '\'' +
                ", description='" + description + '\'' +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }

}
