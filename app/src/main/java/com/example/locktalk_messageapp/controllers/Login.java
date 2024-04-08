package com.example.locktalk_messageapp.controllers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.models.DirHandler;
import com.example.locktalk_messageapp.models.OrganizationHandler;
import com.example.locktalk_messageapp.qolfunctions.FirebaseFunctions;
import com.example.locktalk_messageapp.qolfunctions.GeneralFunctions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



// Login Page

public class Login extends AppCompatActivity {
    // Initialize Variables
    EditText email;
    EditText password;
    Button login;
    TextView signup;
    FirebaseAuth mAuth;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    Double[] userLocation = new Double[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions();
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        // Relate Variables to layout file
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginbtn);
        signup = findViewById(R.id.signup_prompt);
        mAuth = FirebaseAuth.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Handler for when login button is pressed
        login.setOnClickListener((v -> {
            String email_string = email.getText().toString();
            String password_string = password.getText().toString();
            if (TextUtils.isEmpty(email_string) || !Patterns.EMAIL_ADDRESS.matcher(email_string).matches()) {
                email.setError("Invalid Email");
            } else if (TextUtils.isEmpty(password_string)) {
                password.setError("Please Enter Your Password");
            } else if(!checkPermissions()) {
                requestPermissions();
                Toast.makeText(Login.this, "Enable Location Permissions", Toast.LENGTH_SHORT).show();
            } else {
                // Firebase Auth Login Check
                mAuth.signInWithEmailAndPassword(email_string, password_string).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String UID = user.getUid();
                        FirebaseFunctions.allEmp().document(UID).get().addOnCompleteListener(task2 -> {
                            //Geo-fence check
                            if (task2.isSuccessful()) {
                                DirHandler model = task2.getResult().toObject(DirHandler.class);
                                FirebaseFunctions.getLocation(model.getOrg()).get().addOnCompleteListener(task3 -> {
                                    OrganizationHandler org = task3.getResult().toObject(OrganizationHandler.class);
                                    Double[] orgLocation = new Double[2];
                                    orgLocation[0] = org.getLatitude();
                                    orgLocation[1] = org.getLongitude();
                                    getLastLocation();


                                    boolean inBounds = GeneralFunctions.checkLocationBounds(orgLocation, userLocation);
                                    if (inBounds) {
                                        Toast.makeText(Login.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, Home.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(Login.this, "User Out of Bounds for Org: " + org.getName(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

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

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            userLocation[0] = location.getLatitude();
                            userLocation[1] = location.getLongitude();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            userLocation[0] = mLastLocation.getLatitude();
            userLocation[1] = mLastLocation.getLongitude();
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
        }