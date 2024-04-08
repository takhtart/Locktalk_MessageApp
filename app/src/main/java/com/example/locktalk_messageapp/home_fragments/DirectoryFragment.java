package com.example.locktalk_messageapp.home_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.adapters.DirAdapter;
import com.example.locktalk_messageapp.models.DirHandler;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

// Directory Page Fragment of the Main Home Page (Bottom Nav Bar)

public class DirectoryFragment extends Fragment {

    // Initialize Variables
    RecyclerView directoryList;

    DirAdapter dirAdapter;



    public DirectoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_directory, container, false);
        // Relate Variables to layout file
        directoryList = view.findViewById(R.id.dir_list);

        // Call Setup
        setupDirListView();

        return view;
    }

    // Handles Compiling of things to display in the RecyclerView
    void setupDirListView() {

        FirestoreRecyclerOptions<DirHandler> options =
                new FirestoreRecyclerOptions.Builder<DirHandler>()
                        .setQuery(FirebaseFirestore.getInstance().collection("employees"), DirHandler.class)
                        .build();

        dirAdapter = new DirAdapter(options,getContext());
        directoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        directoryList.setAdapter(dirAdapter);
        dirAdapter.startListening();


    }

    // RecyclerView Listeners
    @Override
    public void onStart() {
        super.onStart();
        if(dirAdapter !=null){
            dirAdapter.startListening();

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(dirAdapter !=null){
            dirAdapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(dirAdapter !=null){
            dirAdapter.notifyDataSetChanged();
        }
    }
}