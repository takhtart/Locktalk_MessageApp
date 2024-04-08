package com.example.locktalk_messageapp.controllers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.home_fragments.DirectoryFragment;
import com.example.locktalk_messageapp.home_fragments.MessagesFragment;
import com.example.locktalk_messageapp.home_fragments.ProfileFragment;
import com.example.locktalk_messageapp.qolfunctions.FirebaseFunctions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;


// Home Page Activity that handles 3 subpages (Fragments - On Bottom Nav Bar)

public class Home extends AppCompatActivity {

    // Initialize Variables
    Button logout;

    BottomNavigationView bottomnav;

    ImageButton search;

    MessagesFragment messagesFragment;
    DirectoryFragment directoryFragment;

    ProfileFragment profileFragment;
    int PERMISSION_ID = 44;
    Double[] userLocation = new Double[2];
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        search = findViewById(R.id.search_button);

        //Initialize Fragments
        messagesFragment = new MessagesFragment();
        directoryFragment = new DirectoryFragment();
        profileFragment = new ProfileFragment();

        bottomnav = findViewById(R.id.bottomnavbar);


        // Switches between fragments when accessed on bottom nav row
        bottomnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.navigation_messages){
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_frame,messagesFragment).commit();

                    // Sets search button visibility on the fragment
                    search.setEnabled(false);
                    search.setVisibility(View.INVISIBLE);
                }
                if(item.getItemId()==R.id.navigation_directory){
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_frame,directoryFragment).commit();

                    // Sets search button visibility on the fragment
                    search.setEnabled(true);
                    search.setVisibility(View.VISIBLE);
                }
                if(item.getItemId()==R.id.navigation_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_frame,profileFragment).commit();

                    // Sets search button visibility on the fragment
                    search.setEnabled(false);
                    search.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });

        //Sets Default Fragment to Messages
        bottomnav.setSelectedItemId(R.id.navigation_messages);

        // Goes to Search Page If Search Button is Pressed
        search.setOnClickListener(v -> {
            if(bottomnav.getSelectedItemId()==R.id.navigation_directory){
                Intent intent = new Intent(Home.this, SearchDir.class);
                startActivity(intent);
            }

        });

        // Logout if logout is pressed
        logout = findViewById(R.id.logoutbtn);

        logout.setOnClickListener((v ->{
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(Home.this, Login.class);
                        startActivity(intent);
                    }
                }
            });


        }));

        getFCMtoken();

        // Requests Notification Permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},99);
        }



    }
    @Override
    public void onStart(){
        super.onStart();
        if(!checkPermissions()) {
            requestPermissions();
            Toast.makeText(Home.this, "Enable Location Permissions", Toast.LENGTH_SHORT).show();
            finish();
            System.exit(0);
        }
    }
    // Checks if Requested Notification Permissions were granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 99){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i("NotificationGranted","true");
            }
            else{
                Log.i("NotificationGranted","false");
            }
        }
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    // Gets token from FCM
    void getFCMtoken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String FCMtoken = task.getResult();
                FirebaseFunctions.currentUser().update("FCMToken",FCMtoken);
            }
        });
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
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

}