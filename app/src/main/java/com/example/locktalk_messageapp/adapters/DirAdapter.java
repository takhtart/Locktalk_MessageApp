package com.example.locktalk_messageapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.controllers.Chat;
import com.example.locktalk_messageapp.models.DirHandler;
import com.example.locktalk_messageapp.qolfunctions.FirebaseFunctions;
import com.example.locktalk_messageapp.qolfunctions.GeneralFunctions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


// Adapter That Handles The RecyclerView For The Directory and Search Directory Pages
public class DirAdapter extends FirestoreRecyclerAdapter<DirHandler, DirAdapter.showres> {

    Context context;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public DirAdapter(@NonNull FirestoreRecyclerOptions<DirHandler> options, Context context) {
        super(options);
        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull showres holder, int position, @NonNull DirHandler model) {
        String fullname = model.getFirst_Name() + " " + model.getLast_Name();

        holder.name.setText(fullname);
        holder.email.setText(model.getEmail());

        if(model.getUserID().equals(FirebaseFunctions.currentUID())){
            holder.name.setText(fullname + " (Me)");
        };

        holder.itemView.setOnClickListener(v -> {

            if(!model.getUserID().equals(FirebaseFunctions.currentUID())){
                Intent intent = new Intent(context, Chat.class);
                GeneralFunctions.passuserinfo(intent,model);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            };
        });

    }

    @NonNull
    @Override
    public showres onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_dir_row,parent,false);
        return new showres(view);
    }

    class showres extends RecyclerView.ViewHolder{
        TextView name, email;

        public showres(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.ResultName);
            email = itemView.findViewById(R.id.ResultEmail);
        }
    }
}
