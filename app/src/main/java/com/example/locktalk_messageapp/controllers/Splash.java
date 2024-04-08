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

public class Splash extends AppCompatActivity {

    // Initialize Variables
    FirebaseAuth mAuth;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    Double[] userLocation = new Double[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Get Firebase Authorization Status
        mAuth = FirebaseAuth.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

    }

    @Override
    public void onStart() {
        super.onStart();
        requestPermissions();
        // Check if user is signed in
        FirebaseUser user = mAuth.getCurrentUser();
        //Location check
        if(user != null) {
            String UID = user.getUid();
            if(!checkPermissions()) {
                requestPermissions();
                Toast.makeText(Splash.this, "Enable Location Permissions", Toast.LENGTH_SHORT).show();
                finish();
                System.exit(0);
            }
            FirebaseFunctions.allEmp().document(UID).get().addOnCompleteListener(task2 -> {
                if(task2.isSuccessful()){
                    DirHandler model = task2.getResult().toObject(DirHandler.class);
                    FirebaseFunctions.getLocation(model.getOrg()).get().addOnCompleteListener(task3 -> {
                        OrganizationHandler org = task3.getResult().toObject(OrganizationHandler.class);
                        Double[] orgLocation = new Double[2];
                        orgLocation[0] = org.getLatitude();
                        orgLocation[1] = org.getLongitude();
                        getLastLocation();
                        boolean inBounds = GeneralFunctions.checkLocationBounds(orgLocation, userLocation);
                        if(inBounds){
                            if(getIntent().getExtras()!= null){
                                String UID2 = getIntent().getExtras().getString("EmpID");
                                FirebaseFunctions.allEmp().document(UID2).get().addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        DirHandler model2 = task.getResult().toObject(DirHandler.class);
                                        Intent homeintent = new Intent(this, Home.class);
                                        homeintent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(homeintent);
                                        Intent intent = new Intent(this, Chat.class);
                                        GeneralFunctions.passuserinfo(intent,model2);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                            else {
                                startActivity(new Intent(Splash.this, Home.class));
                            }
                        }
                        else {
                            Toast.makeText(Splash.this, "User Out of Bounds for Org: " + org.getName(), Toast.LENGTH_LONG).show();
                            //redirect to login
                            startActivity(new Intent(Splash.this, Login.class));
                        }
                    });
                }
            });
        }
        else {
            startActivity(new Intent(Splash.this, Login.class));
        }
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
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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