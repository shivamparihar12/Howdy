package com.example.howdy.model;

public class Message {
    private String userID, message;
    long timeStamp;

    public String getUserID() {
        return userID;
    }

    public String getMessage() {
        return message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Message(String userID, String message) {
        this.userID = userID;
        this.message = message;
    }

    public Message(String userID, String message, long timeStamp) {
        this.userID = userID;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public Message() {
    }
}
