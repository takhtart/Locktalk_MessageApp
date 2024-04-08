package com.example.locktalk_messageapp.adapters;

import static androidx.core.content.ContextCompat.startActivity;

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
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import com.example.locktalk_messageapp.controllers.Chat;
import com.example.locktalk_messageapp.controllers.Login;
import com.example.locktalk_messageapp.models.ChatRoomHandler;
import com.example.locktalk_messageapp.qolfunctions.EncryptionManager;
import com.example.locktalk_messageapp.qolfunctions.FirebaseFunctions;
import com.example.locktalk_messageapp.qolfunctions.GeneralFunctions;
import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.models.DirHandler;
import com.example.locktalk_messageapp.qolfunctions.KdcCodeWorker;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.concurrent.TimeUnit;

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

//               holder.itemView.setOnClickListener(v -> {
//
//                   if(!emp2.getUserID().equals(FirebaseFunctions.currentUID())){
//                       Intent intent = new Intent(context, Chat.class);
//                       GeneralFunctions.passuserinfo(intent,emp2);
//                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                       context.startActivity(intent);
//                   };
//               });
               holder.itemView.setOnClickListener(v -> {
                   FirebaseFunctions.currentUser().get().addOnCompleteListener(task2 -> {
                       if (task.isSuccessful()) {
                           DirHandler currentUser = task.getResult().toObject(DirHandler.class);
                           if (!emp2.getUserID().equals(FirebaseFunctions.currentUID())) {
                               if (currentUser.getKdcKey().equals(emp2.getKdcKey())) {
                                   // KDC keys match, proceed to the Chat activity
                                   Intent intent = new Intent(context, Chat.class);
                                   GeneralFunctions.passuserinfo(intent, emp2);
                                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                   PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(KdcCodeWorker.class, 1, TimeUnit.HOURS)
                                           .setInitialDelay(1, TimeUnit.MINUTES); // Start immediately

                                   PeriodicWorkRequest workRequest = builder.build();
                                   WorkManager.getInstance().enqueue(workRequest);
                                   context.startActivity(intent);
                               } else {
                                   // KDC keys do not match, handle the case (e.g., show an error or log out)
                                   handleKdcKeyMismatch();
                               }
                           }
                       } else {
                           Log.e("MessagesAdapter", "Failed to get current user's KDC key.", task.getException());
                           // Handle the failure to get current user's KDC key
                       }
                   });
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

    private void handleKdcKeyMismatch() {
        // Log the error for debugging purposes
        Log.e("MessagesAdapter", "KDC key mismatch detected. Access denied.");

        // Create an intent to start the Login activity
        Intent intent = new Intent(context, Login.class);

        // Set flags to clear the task stack and start a new task with the Login activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Start the activity with the intent
        context.startActivity(intent);
    }


}
