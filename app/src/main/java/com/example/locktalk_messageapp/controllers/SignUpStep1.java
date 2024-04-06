package com.example.locktalk_messageapp.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import com.example.locktalk_messageapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpStep1 extends AppCompatActivity {

    // Initialize Variables
    EditText email;

    Button next;

    Button back;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step1);

        email = findViewById(R.id.email);

        db = FirebaseFirestore.getInstance();

        // Pass email to signup if typed in login
        if(!getIntent().getExtras().getString("email").isEmpty()){
            email.setText(getIntent().getExtras().getString("email"));
        }

        next = findViewById(R.id.step2btn);

        back = findViewById(R.id.backbtn1);

        next.setOnClickListener((v ->{

            // Check if email is of the right format
            String email_string = email.getText().toString();
            if(TextUtils.isEmpty(email_string) || !Patterns.EMAIL_ADDRESS.matcher(email_string).matches()){
                email.setError("Invalid Email");
                return;
            }
            Intent intent = new Intent(SignUpStep1.this, SignUpStep2.class);
            startActivity(intent);

        }));


        // Go Back To Login Page if Pressed
        back.setOnClickListener((v ->{
            Intent intent = new Intent(SignUpStep1.this, Login.class);
            intent.putExtra("email",email.getText().toString());
            startActivity(intent);

        }));


    }
}