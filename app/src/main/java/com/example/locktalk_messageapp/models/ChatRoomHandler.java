package com.example.locktalk_messageapp.models;

import android.util.Base64;

import com.google.firebase.Timestamp;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
        String ret = "";
        try {
            ret = decrypt(lastmessage);
        }
        catch (Exception e) {}
        return ret;
    }

    public void setLastmessage(String lastmessage) {
        try {
            this.lastmessage = encrypt(lastmessage);
        }
        catch (Exception e) {}
    }

    // Encrypts message content using Blowfish algo and chatID as shared key
    public String encrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(chatID.getBytes(), "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec("abcdefgh".getBytes()));
        byte[] values = cipher.doFinal(message.getBytes());
        return Base64.encodeToString(values, Base64.DEFAULT);
    }

    // Decrypts message content using Blowfish algo and chatID as shared key
    public String decrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] values = Base64.decode(message, Base64.DEFAULT);
        SecretKeySpec secretKeySpec = new SecretKeySpec(chatID.getBytes(), "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5PAdding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec("abcdefgh".getBytes()));
        return new String(cipher.doFinal(values));
    }
}
