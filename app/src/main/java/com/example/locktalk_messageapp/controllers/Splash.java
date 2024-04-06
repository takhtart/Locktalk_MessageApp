package com.example.locktalk_messageapp.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.models.DirHandler;
import com.example.locktalk_messageapp.qolfunctions.FirebaseFunctions;
import com.example.locktalk_messageapp.qolfunctions.GeneralFunctions;
import com.google.firebase.auth.*;

public class Splash extends AppCompatActivity {

    // Initialize Variables
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Get Firebase Authorization Status
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in
        FirebaseUser user = mAuth.getCurrentUser();

        // Check if user has accessed app via notification
        if(user != null && getIntent().getExtras()!= null){
            String UID = getIntent().getExtras().getString("EmpID");
            FirebaseFunctions.allEmp().document(UID).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DirHandler model = task.getResult().toObject(DirHandler.class);
                    Intent homeintent = new Intent(this, Home.class);
                    homeintent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(homeintent);
                    Intent intent = new Intent(this, Chat.class);
                    GeneralFunctions.passuserinfo(intent,model);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
        // If user opens app normally check if logged in
        else if(user != null){
            startActivity(new Intent(Splash.this, Home.class));
        }
        else{
            startActivity(new Intent(Splash.this, Login.class));
        }


    }
}