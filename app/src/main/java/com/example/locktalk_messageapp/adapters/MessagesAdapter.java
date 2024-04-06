package com.example.locktalk_messageapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.locktalk_messageapp.controllers.Chat;
import com.example.locktalk_messageapp.models.ChatRoomHandler;
import com.example.locktalk_messageapp.qolfunctions.FirebaseFunctions;
import com.example.locktalk_messageapp.qolfunctions.GeneralFunctions;
import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.models.DirHandler;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

// Adapter That Handles The RecyclerView For The Messages Page
public class MessagesAdapter extends FirestoreRecyclerAdapter<ChatRoomHandler,MessagesAdapter.showchatlist> {

    Context context;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MessagesAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomHandler> options,Context context) {
        super(options);
        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull showchatlist holder, int position, @NonNull ChatRoomHandler model) {
        FirebaseFunctions.getEmpInfoFromChat(model.getEmpIDs()).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){


               DirHandler emp2 = task.getResult().toObject(DirHandler.class);
               holder.name.setText(emp2.getFirst_Name()+" "+emp2.getLast_Name());

               holder.messsage.setText(model.getLastmessage());

               Log.i("OG Time",model.getMessagetimestamp().toString());
               holder.time.setText(FirebaseFunctions.TimestampToTime(model.getMessagetimestamp()));
               Log.i("Mod Time",FirebaseFunctions.TimestampToTime(model.getMessagetimestamp()));

               if(model.getLastmessageId().equals(FirebaseFunctions.currentUID())){
                   holder.messsage.setText("(Me) " + model.getLastmessage());
               }
               else{
                   holder.messsage.setText(model.getLastmessage());
               }

               holder.itemView.setOnClickListener(v -> {

                   if(!emp2.getUserID().equals(FirebaseFunctions.currentUID())){
                       Intent intent = new Intent(context, Chat.class);
                       GeneralFunctions.passuserinfo(intent,emp2);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       context.startActivity(intent);
                   };
               });

           }
        });

    }

    @NonNull
    @Override
    public showchatlist onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_row,parent,false);
        return new showchatlist(view);
    }

    class showchatlist extends RecyclerView.ViewHolder{
        TextView name;
        TextView messsage;
        TextView time;
        ImageView prof_pic;
        public showchatlist(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.ChatName);
            messsage = itemView.findViewById(R.id.ChatLastMessage);
            time = itemView.findViewById(R.id.ChatLastMessageTimestamp);
            prof_pic = itemView.findViewById(R.id.user_icon);

        }
    }


}
