package com.example.locktalk_messageapp.qolfunctions;

import android.util.Base64;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FirebaseFunctions {

    // Gets current ID of the Logged In User (Firebase Auth)
    public static String currentUID() {

        return FirebaseAuth.getInstance().getUid();
    }

    // Gets User Info Using the current ID of the User matching it with the database (Firebase Auth ID and Firebase Cloud DB document ID MUST BE THE SAME)
    public static DocumentReference currentUser(){
        return FirebaseFirestore.getInstance().collection("employees").document(currentUID());
    }

    // Pulls the list of all the users in the employees database
    public static CollectionReference allEmp(){
        return FirebaseFirestore.getInstance().collection("employees");
    }

    // Pulls the specific chat document using the chatID
    public static DocumentReference getChatRoom(String chatID){
        return  FirebaseFirestore.getInstance().collection("chats").document(chatID);


    }

    // Pulls the list of all messages in the chats database pertaining to the chatID
    public static CollectionReference getMessageRef(String chatID){
        return getChatRoom(chatID).collection("messages");
    }

    // Pulls the list of all chats in the chats database
    public static CollectionReference getChats(){
        return FirebaseFirestore.getInstance().collection("chats");
    }

    // Used to generate a chatroomID, using a combination of both employees ID's
    public static String getChatRoomID(String empID1, String empID2){
        if(empID1.hashCode()<empID2.hashCode()){
            return empID1+empID2;
        }
        else {
            return empID2+empID1;
        }
    }

    // Used to get the other employee's information (the one being sent the messages)
    public static DocumentReference getEmpInfoFromChat(List<String> UIDs){
        if(UIDs.get(0).equals(FirebaseFunctions.currentUID())){
            return allEmp().document(UIDs.get(1));
        }
        return allEmp().document(UIDs.get(0));

    }

    // Takes firebase timestamp and converts it to 12hr format time (used for recent messages)
    public static String TimestampToTime(Timestamp timestamp){

        return new SimpleDateFormat("h:mm aa").format(timestamp.toDate());

    }

    public static DocumentReference getLocation(String orgID){
        return FirebaseFirestore.getInstance().collection("locations").document(orgID);
    }


}
