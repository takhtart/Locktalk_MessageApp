package com.example.locktalk_messageapp.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.locktalk_messageapp.R;

public class SignUpStep2 extends AppCompatActivity {

    Button back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        back = findViewById(R.id.backbtn1);

        // Go back to login page on press
        back.setOnClickListener((v ->{

            Intent intent = new Intent(SignUpStep2.this, Login.class);
            startActivity(intent);

        }));
    }
}