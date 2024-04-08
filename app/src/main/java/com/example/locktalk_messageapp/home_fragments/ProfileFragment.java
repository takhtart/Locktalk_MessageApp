package com.example.locktalk_messageapp.home_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.controllers.Login;
import com.example.locktalk_messageapp.models.DirHandler;
import com.example.locktalk_messageapp.qolfunctions.FirebaseFunctions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

// Profile Page Fragment of the Main Home Page (Bottom Nav Bar)

public class ProfileFragment extends Fragment {

    // Initialize Variables
    ImageView profPic;
    TextView name;
    TextView email;

    Button logoutbtn;

    DirHandler dirHandler;

    public ProfileFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Relate Variables to layout file
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        profPic = view.findViewById(R.id.user_icon);
        name = view.findViewById(R.id.YourName);
        email = view.findViewById(R.id.YourEmail);
        logoutbtn = view.findViewById(R.id.logoutbtnprof);

        // Retrieves User's Personal Info
        getUserInfo();

        // Listens For Logout Button Press
        logoutbtn.setOnClickListener((v ->{
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getContext(), Login.class);
                        startActivity(intent);
                    }
                }
            });


        }));

        return view;
    }

    // Retrieves User's Personal Info (One who is logged in)
    void getUserInfo(){
        FirebaseFunctions.currentUser().get().addOnCompleteListener(task -> {
            dirHandler = task.getResult().toObject(DirHandler.class);
            name.setText(dirHandler.getFirst_Name()+" "+ dirHandler.getLast_Name() );
            email.setText(dirHandler.getEmail());
        });

    }
}