package com.example.howdy.model;

public class Users {
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    private String phoneNo;
    private String userID;

    public void setUserID(String userID) {
        this.userID = userID;
    }

    private String profilePicture;
    private String lastSeen;

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public String getUserID() {
        return userID;
    }

    public Users() {
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }


    public Users(String username, String phoneNo, String userID, String profilePicture, String lastSeen, String userProfileImage) {
        this.username = username;
        this.phoneNo = phoneNo;
        this.userID = userID;
        this.profilePicture = profilePicture;
        this.lastSeen = lastSeen;
        this.userProfileImage = userProfileImage;
    }

    public Users(String username, String phoneNo, String userID, String userProfileImage) {
        this.username = username;
        this.phoneNo = phoneNo;
        this.userID = userID;
        this.userProfileImage = userProfileImage;
    }

    private String userProfileImage;

    public Users(String phoneNo, String userID) {
        this.phoneNo = phoneNo;
        this.userID = userID;
    }

    public void UsersNameAndPhone(String phoneNo, String username) {
        this.phoneNo = phoneNo;
        this.username = username;
    }
}
