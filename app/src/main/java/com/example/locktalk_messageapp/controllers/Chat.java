package com.example.locktalk_messageapp.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.locktalk_messageapp.R;
import com.example.locktalk_messageapp.adapters.ChatAdapter;
import com.example.locktalk_messageapp.models.ChatRoomHandler;
import com.example.locktalk_messageapp.models.MessageHandler;
import com.example.locktalk_messageapp.models.DirHandler;
import com.example.locktalk_messageapp.qolfunctions.FirebaseFunctions;
import com.example.locktalk_messageapp.qolfunctions.EncryptionManager;
import com.example.locktalk_messageapp.qolfunctions.GeneralFunctions;
import com.example.locktalk_messageapp.qolfunctions.KdcCodeWorker;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// Chat Conversation Page

public class Chat extends AppCompatActivity {

    // Initialize Variables
    EditText message;
    DirHandler emp2;
    RecyclerView chats;
    ImageButton sendbtn;
    TextView emp2name;
    ImageButton backbtn;
    String chatRoomID;
    ChatRoomHandler chatroom;
    ChatAdapter chatAdapter;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        // Get other user's info
        emp2 = GeneralFunctions.getUserInfo(getIntent());

        // Relate Variables to layout file
        chats = findViewById(R.id.chat_disp);
        message = findViewById(R.id.message);
        sendbtn = findViewById(R.id.sendbtn);
        emp2name = findViewById(R.id.emp2NameChat);
        backbtn = findViewById(R.id.backbtnchat);
        chatRoomID = FirebaseFunctions.getChatRoomID(FirebaseFunctions.currentUID(),emp2.getUserID());
        verifyKdcKeyAndSetUpChat();
        // Back on Press
        backbtn.setOnClickListener(v -> {
               getOnBackPressedDispatcher().onBackPressed();
        });

        // Sets Name At the Top of The Chat
        emp2name.setText(emp2.getFirst_Name()+" "+emp2.getLast_Name());

        // Creates A Chat Room
//        createChatRoom();

        // Button That Handles Sending a Message
        sendbtn.setOnClickListener(v -> {
            String messageinput = message.getText().toString().trim();

            if(messageinput.isEmpty()){
                return;
            }
            sendMessage(messageinput);
        });

        // Displays Chat
//        setupChatViews();

    }

    // Sets Up Chat For Display
   void setupChatViews(){

        // Queries DB for all messages for a chat
       FirestoreRecyclerOptions<MessageHandler> options =
               new FirestoreRecyclerOptions.Builder<MessageHandler>()
                       .setQuery(FirebaseFunctions.getMessageRef(chatRoomID).orderBy("timestamp", Query.Direction.DESCENDING), MessageHandler.class)
                       .build();

       chatAdapter = new ChatAdapter(options,getApplicationContext());
       LinearLayoutManager llm = new LinearLayoutManager(this);
       llm.setReverseLayout(true);
       chats.setLayoutManager(llm);
       chats.setAdapter(chatAdapter);
       chatAdapter.startListening();
       chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
           @Override
           public void onItemRangeInserted(int positionStart, int itemCount) {
               super.onItemRangeInserted(positionStart, itemCount);
               chats.smoothScrollToPosition(-5);
           }
       });


    }

    // Accesses Chat Room (If One Exists) Or Creates A New One
    void createChatRoom(){
        FirebaseFunctions.getChatRoom(chatRoomID).get().addOnCompleteListener(task ->  {
            if(task.isSuccessful()){
                chatroom = task.getResult().toObject(ChatRoomHandler.class);
                if(chatroom ==null){
                    chatroom = new ChatRoomHandler(chatRoomID, Arrays.asList(FirebaseFunctions.currentUID(),emp2.getUserID()), Timestamp.now(), "","");
                    FirebaseFunctions.getChatRoom(chatRoomID).set(chatroom);
                }
            }
        });
    }

    //Sends Message And Triggers Notification
    void sendMessage(String messageinput){

        try {
            String encryptedmessage = EncryptionManager.encrypt(messageinput, chatRoomID);

            chatroom.setMessagetimestamp(Timestamp.now());
            chatroom.setLastmessageId(FirebaseFunctions.currentUID());
            chatroom.setLastmessage(encryptedmessage);
            FirebaseFunctions.getChatRoom(chatRoomID).set(chatroom);

            MessageHandler messageHandler = new MessageHandler(encryptedmessage, FirebaseFunctions.currentUID(), chatRoomID, Timestamp.now());

            FirebaseFunctions.getMessageRef(chatRoomID).add(messageHandler).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        message.setText("");
                        sendNotification(messageinput);
                    }

                }
            });
        } catch (Exception e) {}
    }

    //Creates and sends JSON Object Containing Details Of The Notification
    void sendNotification(String messageinput){
        FirebaseFunctions.currentUser().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DirHandler emp1 = task.getResult().toObject(DirHandler.class);

                try {
                    JSONObject jsonObject  = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    String name = emp1.getFirst_Name() + " " + emp1.getLast_Name();
                    notificationObj.put("title",name);
                    notificationObj.put("body",messageinput);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("EmpID",emp1.getUserID());

                    jsonObject.put("notification",notificationObj);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",emp2.getFCMToken());

                    callFCMAPI(jsonObject);

                    Log.i("notification_message", String.valueOf(jsonObject));

                }catch (Exception e){

                }

            }
        });
    }


    // Sends Notification Using FCM API + OKHttp
    void callFCMAPI(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json");
        String apiurl = "https://fcm.googleapis.com/fcm/send";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder().url(apiurl).post(body).header("Authorization", "Bearer AAAA73ueqtA:APA91bEmwJAPeRAs8pkFItRZo_i7S-f6W7m0SbauL0lMAuajzDgrbsfBpyzU1EZvf43mjL7ukrQ2bZo8-Sz7mPjC5sCPJ45f43cfNEfwLlcUatLcmEmigFVfrSf_flimytUBNp_CAcWq").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("SendNotification","Fail");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.i("SendNotification","Success");

            }
        });

    }

    private void verifyKdcKeyAndSetUpChat() {
        // Get the current user's KDC key
        FirebaseFunctions.currentUser().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DirHandler emp1 = task.getResult().toObject(DirHandler.class);
                // Now get the other user's KDC key
                FirebaseFunctions.allEmp().document(emp2.getUserID()).get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        DirHandler emp2WithKey = task2.getResult().toObject(DirHandler.class);
                        // Compare both KDC keys
                        assert emp1 != null;
                        assert emp2WithKey != null;
                        if (emp1.getKdcKey().equals(emp2WithKey.getKdcKey())) {
                            // Keys match, set up the chat views
                            setupChatViews();
                            createChatRoom();
//                            PeriodicWorkRequest kdcCodeWorkRequest = new PeriodicWorkRequest.Builder(KdcCodeWorker.class, 1, TimeUnit.HOURS)
//                                    .build();
//                            WorkManager.getInstance(this).enqueue(kdcCodeWorkRequest);
                            PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(KdcCodeWorker.class, 1, TimeUnit.HOURS)
                                    .setInitialDelay(1, TimeUnit.MINUTES); // Start immediately

                            PeriodicWorkRequest workRequest = builder.build();
                            WorkManager.getInstance(this).enqueue(workRequest);
                        } else {

                            redirectToLoginOrHome();
                        }
                    }
                    else{
                        Log.e("Chat", "Failed to get other employee's KDC key.", task2.getException());
                    }

                });
            } else {
                Log.e("Chat", "Failed to get current user's KDC key.");
                // Handle failure to get KDC key
            }
        });
    }

    private void redirectToLoginOrHome() {
        // Implement your logic to redirect to the Login or Home page
        // For instance:
        Intent intent = new Intent(Chat.this, Login.class);
        startActivity(intent);
        finish();
    }
}