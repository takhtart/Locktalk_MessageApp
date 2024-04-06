package com.example.locktalk_messageapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.locktalk_messageapp.qolfunctions.FirebaseFunctions;
import com.example.locktalk_messageapp.models.MessageHandler;
import com.example.locktalk_messageapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


// Adapter That Handles The RecyclerView For The Chat Page
public class ChatAdapter extends FirestoreRecyclerAdapter<MessageHandler,ChatAdapter.ShowMessages> {

    Context context;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<MessageHandler> options,Context context) {
        super(options);
        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull ShowMessages holder, int position, @NonNull MessageHandler model) {
        if(model.getOriginID().equals(FirebaseFunctions.currentUID())){
            holder.left_bubble.setVisibility(View.GONE);
            holder.right_bubble.setVisibility(View.VISIBLE);
            holder.right_bubble_text.setText(model.getMessage());
        }
        else{
            holder.left_bubble.setVisibility(View.VISIBLE);
            holder.right_bubble.setVisibility(View.GONE);
            holder.left_bubble_text.setText(model.getMessage());
        }

    }

    @NonNull
    @Override
    public ShowMessages onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_bubble_row,parent,false);
        return new ShowMessages(view);
    }

    class ShowMessages extends RecyclerView.ViewHolder{
        LinearLayout left_bubble,right_bubble;
        TextView left_bubble_text, right_bubble_text;

        public ShowMessages(@NonNull View itemView) {
            super(itemView);
            left_bubble = itemView.findViewById(R.id.left_bubble);
            right_bubble = itemView.findViewById(R.id.right_bubble);

            left_bubble_text = itemView.findViewById(R.id.left_bubble_text);
            right_bubble_text = itemView.findViewById(R.id.right_bubble_text);


        }
    }
}

