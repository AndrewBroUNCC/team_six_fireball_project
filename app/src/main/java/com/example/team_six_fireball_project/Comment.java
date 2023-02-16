package com.example.team_six_fireball_project;

public class Comment {
    //comment commenterName topic commenterID
    String comment;
    String commenterName;
    String topic;
    String commenterID;

    public Comment() {
    }

    public Comment(String comment, String commenterName, String topic, String commenterID) {
        this.comment = comment;
        this.commenterID = commenterID;
        this.commenterName = commenterName;
        this.topic = topic;

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCommenterID() {
        return commenterID;
    }

    public void setCommenterID(String commenterID) {
        this.commenterID = commenterID;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", commenterName='" + commenterName + '\'' +
                ", topic='" + topic + '\'' +
                ", commenterID='" + commenterID + '\'' +
                '}';
    }

}
