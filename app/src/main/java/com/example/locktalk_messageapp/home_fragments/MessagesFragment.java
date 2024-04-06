package com.example.locktalk_messageapp.home_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.locktalk_messageapp.qolfunctions.FirebaseFunctions;
import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.adapters.MessagesAdapter;
import com.example.locktalk_messageapp.models.ChatRoomHandler;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

// Messages Page Fragment of the Main Home Page (Bottom Nav Bar)

public class MessagesFragment extends Fragment {

    // Initialize Variables
    RecyclerView chatsList;

    MessagesAdapter messagesAdapter;

    

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        // Relate Variables to layout file
        chatsList = view.findViewById(R.id.chat_list);

        // Call Setup
        setupChatListView();

        return view;
    }

    // Handles Compiling of things to display in the RecyclerView
    void setupChatListView() {

        FirestoreRecyclerOptions<ChatRoomHandler> options =
                new FirestoreRecyclerOptions.Builder<ChatRoomHandler>()
                        .setQuery(FirebaseFunctions.getChats().whereArrayContains("empIDs", FirebaseFunctions.currentUID()).orderBy("messagetimestamp", Query.Direction.DESCENDING), ChatRoomHandler.class)
                        .build();

        messagesAdapter = new MessagesAdapter(options,getContext());
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        chatsList.setAdapter(messagesAdapter);
        messagesAdapter.startListening();


    }

    // RecyclerView Listeners
    @Override
    public void onStart() {
        super.onStart();
        if(messagesAdapter!=null){
            messagesAdapter.startListening();

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(messagesAdapter!=null){
            messagesAdapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(messagesAdapter!=null){
            messagesAdapter.notifyDataSetChanged();
        }
    }

}

