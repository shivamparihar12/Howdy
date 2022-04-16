package com.example.howdy.model;

import java.util.ArrayList;

public class StoryModel {
    private String userContact;
    private String userName;
    private String userID;
    private ArrayList<String> storiesLinks;

    public StoryModel(String userContact, String userID, ArrayList<String> storiesLinks, String userName) {
        this.userContact = userContact;
        this.userID = userID;
        this.storiesLinks = storiesLinks;
        this.userName = userName;
    }


    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserContact() {
        return userContact;
    }

    public String getUserID() {
        return userID;
    }

    public ArrayList<String> getStoriesLinks() {
        return storiesLinks;
    }

    public String getUserName() {
        return userName;
    }
}
