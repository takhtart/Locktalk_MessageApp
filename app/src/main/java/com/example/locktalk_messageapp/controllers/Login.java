package com.example.locktalk_messageapp.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locktalk_messageapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


// Login Page

public class Login extends AppCompatActivity {
    // Initialize Variables
    EditText email;
    EditText password;
    Button login;
    TextView signup;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        // Relate Variables to layout file
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginbtn);
        signup = findViewById(R.id.signup_prompt);
        mAuth = FirebaseAuth.getInstance();

        //Handler for when login button is pressed
        login.setOnClickListener((v ->{
            String email_string = email.getText().toString();
            String password_string = password.getText().toString();
            if(TextUtils.isEmpty(email_string) || !Patterns.EMAIL_ADDRESS.matcher(email_string).matches()){
                email.setError("Invalid Email");
                return;
            }
            else if(TextUtils.isEmpty(password_string)){
                password.setError("Please Enter Your Password");
                return;
            }
            else{
                // Firebase Auth Login Check
                mAuth.signInWithEmailAndPassword(email_string,password_string).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Succesfully Logged In",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(Login.this, "Login Unsuccessful",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }





        }));


        // Redirects to SignUpStep1 page if clicked
        signup.setOnClickListener((v ->{
            Intent intent = new Intent(Login.this, SignUpStep1.class);
            intent.putExtra("email",email.getText().toString());
            startActivity(intent);

        }));
    }
}