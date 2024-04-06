package com.example.locktalk_messageapp.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.locktalk_messageapp.R;

public class SignUpStep2 extends AppCompatActivity {

    Button back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step2);

        back = findViewById(R.id.backbtn1);

        // Go back to login page on press
        back.setOnClickListener((v ->{

            Intent intent = new Intent(SignUpStep2.this, Login.class);
            startActivity(intent);

        }));
    }
}