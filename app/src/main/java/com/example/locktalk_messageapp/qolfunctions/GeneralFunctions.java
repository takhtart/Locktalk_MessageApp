package com.example.locktalk_messageapp.qolfunctions;

import android.content.Intent;

import com.example.locktalk_messageapp.models.DirHandler;

public class GeneralFunctions {

    //Retrieves User Info
    public static DirHandler getUserInfo(Intent intent){
        DirHandler emp = new DirHandler();
        emp.setFirst_Name(intent.getStringExtra("First_Name"));
        emp.setLast_Name(intent.getStringExtra("Last_Name"));
        emp.setEmail(intent.getStringExtra("Email"));
        emp.setUserID(intent.getStringExtra("userID"));
        emp.setFCMToken(intent.getStringExtra("FCMToken"));

        return emp;
    }

    //Sends User Info to Intent
    public static void passuserinfo(Intent intent, DirHandler model){

        intent.putExtra("Employee",model.getFirst_Name()+" "+model.getLast_Name());
        intent.putExtra("First_Name", model.getFirst_Name());
        intent.putExtra("Last_Name", model.getLast_Name());
        intent.putExtra("Email",model.getEmail());
        intent.putExtra("userID", model.getUserID());
        intent.putExtra("FCMToken", model.getFCMToken());
    }
}
