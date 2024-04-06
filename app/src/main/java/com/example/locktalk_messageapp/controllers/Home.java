package com.example.locktalk_messageapp.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.home_fragments.DirectoryFragment;
import com.example.locktalk_messageapp.home_fragments.MessagesFragment;
import com.example.locktalk_messageapp.home_fragments.ProfileFragment;
import com.example.locktalk_messageapp.qolfunctions.FirebaseFunctions;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

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


}