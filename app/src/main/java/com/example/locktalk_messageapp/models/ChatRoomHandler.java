package com.example.locktalk_messageapp.models;

import com.google.firebase.Timestamp;

import java.util.List;

// Chat Room Model, Containing a Contstructor, Getters/Setters
public class ChatRoomHandler {
    String chatID;
    List<String> empIDs;
    Timestamp messagetimestamp;

    String lastmessageId;

    String lastmessage;

    ChatRoomHandler(){

    }

    public ChatRoomHandler(String chatID, List<String> empIDs, Timestamp messagetimestamp, String lastmessageId, String lastmessage) {
        this.chatID = chatID;
        this.empIDs = empIDs;
        this.messagetimestamp = messagetimestamp;
        this.lastmessageId = lastmessageId;
        this.lastmessage = lastmessage;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public List<String> getEmpIDs() {
        return empIDs;
    }

    public void setEmpIDs(List<String> empIDs) {
        this.empIDs = empIDs;
    }

    public Timestamp getMessagetimestamp() {
        return messagetimestamp;
    }

    public void setMessagetimestamp(Timestamp messagetimestamp) {
        this.messagetimestamp = messagetimestamp;
    }

    public String getLastmessageId() {
        return lastmessageId;
    }

    public void setLastmessageId(String lastmessageId) {
        this.lastmessageId = lastmessageId;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) { this.lastmessage = lastmessage; }

//    public void setLastmessage(String lastmessage) {
//        try {
//            this.lastmessage = encrypt(lastmessage);
//        }
//        catch (Exception e) {}
//    }
}
