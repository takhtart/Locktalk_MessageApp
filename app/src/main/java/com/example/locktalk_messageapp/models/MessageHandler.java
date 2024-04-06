package com.example.locktalk_messageapp.models;

import com.google.firebase.Timestamp;


// Messages Model, Containing a Contstructor, Getters/Setters
public class MessageHandler {

    private String message;
    private String originID;

    private Timestamp timestamp;


    MessageHandler(){

    }

    public MessageHandler(String message, String originID, Timestamp timestamp) {
        this.message = message;
        this.originID = originID;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOriginID() {
        return originID;
    }

    public void setOriginID(String originID) {
        this.originID = originID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
