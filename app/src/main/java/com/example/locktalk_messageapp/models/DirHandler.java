package com.example.locktalk_messageapp.models;

// Directory Model, Containing a Contstructor, Getters/Setters
public class DirHandler {
    private String First_Name,Last_Name,Email,UserID,FCMToken,Org;


    public DirHandler(){

    }

    public DirHandler(String first_Name, String last_Name, String email, String userID, String FCMToken, String org) {
        First_Name = first_Name;
        Last_Name = last_Name;
        Email = email;
        UserID = userID;
        this.FCMToken = FCMToken;
        Org = org;
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public void setLast_Name(String last_Name) {
        Last_Name = last_Name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }

    public String getOrg() {
        return Org;
    }

    public void setOrg(String Org) {
        this.Org = Org;
    }
}
