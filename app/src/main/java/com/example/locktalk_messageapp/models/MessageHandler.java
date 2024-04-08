package com.example.locktalk_messageapp.models;

import com.google.firebase.Timestamp;


// Messages Model, Containing a Contstructor, Getters/Setters
public class MessageHandler {

    private String message;
    private String originID;

    private String chatID;

    private Timestamp timestamp;


    MessageHandler(){

    }

    public MessageHandler(String message, String originID, String chatID, Timestamp timestamp) {
        this.message = message;
        this.originID = originID;
        this.chatID = chatID;
        this.timestamp = timestamp;
    }

    public String getMessage() { return message; }

//    public String getMessage() throws Exception {
////        return EncryptionManager.decrypt(message, chatID);
//        String ret = "getMessage";
//        try {
//            ret = EncryptionManager.decrypt(message, chatID);
//        } catch (Exception e) {
//            ret = e.getMessage() + " | message is " + message + " | chatID is " + chatID;
//        }
//        return ret;
//    }

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

    public String getChatID() { return chatID; }
}
