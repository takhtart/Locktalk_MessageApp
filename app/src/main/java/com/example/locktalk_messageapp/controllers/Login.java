package com.example.locktalk_messageapp.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.models.DirHandler;
import com.example.locktalk_messageapp.models.OrganizationHandler;
import com.example.locktalk_messageapp.qolfunctions.FirebaseFunctions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;


// Login Page

public class Login extends AppCompatActivity {
    // Initialize Variables
    EditText email;
    EditText password;
    Button login;
    TextView signup;
    FirebaseAuth mAuth;
    DirHandler user;
    OrganizationHandler org;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Relate Variables to layout file
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginbtn);
        signup = findViewById(R.id.signup_prompt);
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Handler for when login button is pressed
        login.setOnClickListener((v -> {
            String email_string = email.getText().toString();
            String password_string = password.getText().toString();
            if (TextUtils.isEmpty(email_string) || !Patterns.EMAIL_ADDRESS.matcher(email_string).matches()) {
                email.setError("Invalid Email");
            } else if (TextUtils.isEmpty(password_string)) {
                password.setError("Please Enter Your Password");
            } else {
                // Firebase Auth Login Check
                mAuth.signInWithEmailAndPassword(email_string, password_string).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //Geo-fence check
                        Double[] orgLocation = getUserOrgLocation();
                        Double[] userLocation = getUserRealLocation();
                        if (checkLocationBounds(orgLocation, userLocation)) {
                            Toast.makeText(Login.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(Login.this, "User out of organization location bounds. Login Unsuccessful", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(Login.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }));


        // Redirects to SignUpStep1 page if clicked
        signup.setOnClickListener((v -> {
            Intent intent = new Intent(Login.this, SignUpStep1.class);
            intent.putExtra("email", email.getText().toString());
            startActivity(intent);

        }));
    }

    private Double[] getUserOrgLocation() {
        Double[] orgLocation = new Double[2];
        FirebaseFunctions.currentUser().get().addOnCompleteListener(task -> {
            user = task.getResult().toObject(DirHandler.class);
        });
        FirebaseFunctions.getLocation(user.getOrg()).get().addOnCompleteListener(task -> {
            org = task.getResult().toObject(OrganizationHandler.class);
            orgLocation[0] = Double.valueOf(org.getLatitude());
            orgLocation[1] = Double.valueOf(org.getLongitude());
        });
        return orgLocation;
    }

    private Double[] getUserRealLocation() {
        Double[] orgLocation = new Double[2];
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            orgLocation[0] = location.getLatitude();
                            orgLocation[1] = location.getLongitude();
                        }
                    }
                });
        return orgLocation;
    }

    private boolean checkLocationBounds(Double[] orgLocation, Double[] userLocation) {
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

        //User must be within 10km of company locations
        return Math.abs(calculatedDistance) < 1;
    }
}