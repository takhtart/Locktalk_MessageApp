package com.example.locktalk_messageapp.controllers;

import android.os.Bundle;
import android.text.Editable;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.adapters.DirAdapter;
import com.example.locktalk_messageapp.models.DirHandler;
import com.example.locktalk_messageapp.qolfunctions.TextChangedListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

// Page that controls searching the directory
public class SearchDir extends AppCompatActivity {

    // Initialize Variables
    EditText searchBox;
    ImageButton back;
    RecyclerView results;
    DirAdapter dirAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searchdir);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        // Relate Variables to layout file
        searchBox = findViewById(R.id.search_box);
        back = findViewById(R.id.backbtnsearch1);
        results = findViewById(R.id.search_results);


        results.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        // Pulls the employee list from the database
        FirestoreRecyclerOptions<DirHandler> options =
                new FirestoreRecyclerOptions.Builder<DirHandler>()
                        .setQuery(FirebaseFirestore.getInstance().collection("employees"), DirHandler.class)
                        .build();

        dirAdapter = new DirAdapter(options,getApplicationContext());
        results.setAdapter(dirAdapter);

        // Opens the Keyboard onto the searchbox
        searchBox.requestFocus();

        // Go back on back press
        back.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // Listens for updated input on search box
        searchBox.addTextChangedListener(new TextChangedListener<EditText>(searchBox) {
            @Override
            public void onTextChanged(EditText target, Editable s){
                String query = searchBox.getText().toString();
                search(query);

            }
        });



    }

    // RecyclerView Listeners
    @Override
    protected void onStart() {
        super.onStart();
        if(dirAdapter !=null){
            dirAdapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dirAdapter !=null){
            dirAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dirAdapter !=null){
            dirAdapter.notifyDataSetChanged();
        }
    }


    // Search Query for Database (Filters Based On Input)
    private void search(String query){
        FirestoreRecyclerOptions<DirHandler> options =
                new FirestoreRecyclerOptions.Builder<DirHandler>()
                        .setQuery(FirebaseFirestore.getInstance().collection("employees").whereGreaterThanOrEqualTo("Email",query).whereLessThanOrEqualTo("Email",query +'\uf8ff'), DirHandler.class)
                        .build();

        dirAdapter = new DirAdapter(options,getApplicationContext());
        results.setAdapter(dirAdapter);
        dirAdapter.startListening();


    }
}