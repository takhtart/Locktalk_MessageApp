package com.example.locktalk_messageapp.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.locktalk_messageapp.R;
// Assume this is a utility class you've created for verifying the KDC code
import com.example.locktalk_messageapp.qolfunctions.KdcCodeVerifier;
import com.example.locktalk_messageapp.qolfunctions.KdcCodeWorker;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

public class KdcEntryActivity extends AppCompatActivity {

    EditText kdcCode;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kdc_entry);

        kdcCode = findViewById(R.id.kdcCode);
        submitButton = findViewById(R.id.submitKdcCodeButton);

        submitButton.setOnClickListener(v -> {
            String code = kdcCode.getText().toString();
            // Use the asynchronous method with a callback
            KdcCodeVerifier.verify(code, new KdcCodeVerifier.VerificationCallback() {
                @Override
                public void onVerificationResult(boolean isVerified) {
                    runOnUiThread(() -> {
                        if (isVerified) {
                            // Code verified successfully, navigate to Home
                            // set the KDC regeneration for every ONE HOUR or on success full login
//                            PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(KdcCodeWorker.class, 1, TimeUnit.HOURS)
//                                    .setInitialDelay(0, TimeUnit.MILLISECONDS); // Start immediately
//
//                            PeriodicWorkRequest workRequest = builder.build();
//                            WorkManager.getInstance().enqueue(workRequest);

                            Intent intent = new Intent(KdcEntryActivity.this, Home.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(KdcEntryActivity.this, "Invalid KDC Code", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
    }
}

