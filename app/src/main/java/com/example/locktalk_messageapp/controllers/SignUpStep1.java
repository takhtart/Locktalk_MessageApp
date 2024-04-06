package com.example.locktalk_messageapp.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.locktalk_messageapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpStep1 extends AppCompatActivity {

    // Initialize Variables
    EditText email;

    Button next;

    Button back;

    FirebaseFirestore db;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step1);

        mAuth = FirebaseAuth.getInstance();

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
            else {
                PasswordReset(email_string);
            }

        }));


        // Go Back To Login Page if Pressed
        back.setOnClickListener((v ->{
            Intent intent = new Intent(SignUpStep1.this, Login.class);
            intent.putExtra("email",email.getText().toString());
            startActivity(intent);

        }));

    }
    void PasswordReset(String email_string){
        Log.i("email:", email_string);
        mAuth.sendPasswordResetEmail(email_string)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SignUpStep1.this, "Reset Password Link has been sent to a registered email", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpStep1.this, SignUpStep2.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpStep1.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}