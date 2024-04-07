package com.example.locktalk_messageapp.qolfunctions;

import android.content.Intent;
import android.util.Log;

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
        emp.setOrg(intent.getStringExtra("Org"));
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
        intent.putExtra("Org", model.getOrg());
    }
    public static boolean checkLocationBounds(Double[] orgLocation, Double[] userLocation) {
        double earthRadius = 3958.75;

        double latDiff = Math.toRadians(userLocation[0] - orgLocation[0]);
        double lngDiff = Math.toRadians(userLocation[1] - orgLocation[1]);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(orgLocation[0])) * Math.cos(Math.toRadians(userLocation[0])) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;
        double calculatedDistance = new Double(distance * meterConversion).floatValue();
        Log.i("User Distance", String.valueOf(calculatedDistance));
        //User must be within 10km of company locations
        return Math.abs(calculatedDistance) < 10000;
    }
}
