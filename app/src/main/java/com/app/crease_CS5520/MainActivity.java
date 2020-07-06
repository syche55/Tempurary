package com.app.crease_CS5520;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.crease_CS5520.data.model.Stickers;
import com.app.crease_CS5520.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mAuth;

    private static final String SERVER_KEY = "AAAA2hXlUTA:APA91bFhRbEipDF4z0LT4DJUC05x5hfL2yFXohyw4KyTQ0OVV349roAvnSPjiwGtrSUI-VO8b3IF55V6itrf-Y5FCtkAXHYq7dw066uqNA7C16UANOysccvY0SJfwrtW0x6LmAcx8UD8";
    private DatabaseReference mDatabase;
    private TextView otherSticker;
    private Button sendSticker;
    private Button getHistory;
    private TextView displayNum;
    private String username;
    private User signOnUser;
    private ListView stickerView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> stickerContainer;
    private ArrayList<String> chatHistory;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView recyclerView;
    private Vibrator vibrator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init sticker view
        otherSticker = (TextView) findViewById(R.id.otherSticker);
        // init database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // init stickers number display
        displayNum = (TextView) findViewById(R.id.displayNum);

        // get the username from login page
        Intent mIntent = getIntent();
        username = mIntent.getExtras().getString("username");

        // first check if user already in database

        mDatabase.child("users").orderByKey().equalTo(username).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                signOnUser = user;
                mDatabase.child("users").child(username).setValue(signOnUser);
                displayNum.setText("Total number of stickers sent: "+String.valueOf(user.history.size()));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled:" + databaseError);
            }
        });

        // if not, then create new user
        if (signOnUser == null) {
            signOnUser = new User(username);
            mDatabase.child("users").child(username).setValue(signOnUser);
        }

        Log.e(TAG, "history size is " + signOnUser.history.size());


        // create user sticker view
        stickerView = findViewById(R.id.stickerView);
        stickerContainer=new ArrayList<>(signOnUser.userSticker.defaultStickerGroup.values());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stickerContainer);


        chatHistory = new ArrayList<>();


        recyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
//        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter(MainActivity.this, chatHistory);
        recyclerView.setAdapter(mAdapter);



        stickerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        // automatically subscribe to news topic
        FirebaseMessaging.getInstance().subscribeToTopic("news")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });


        // get history button
        getHistory = (Button)findViewById(R.id.getHistory);
        getHistory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getHistory(username);
            }
        });

        // vibration
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);



        mDatabase.child("users").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        // test if node added correctly
                        Log.e(TAG, "onChildAdded: dataSnapshot = " + dataSnapshot.getValue());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        User user = dataSnapshot.getValue(User.class);
                        // check if the change made is the login user
                        if (user.username.equalsIgnoreCase(username)){
                            signOnUser = user;
                        }
                        // display username and sticker

                        String display = user.history.size() > 0 ? user.username + ": " + user.history.get(user.history.size() - 1) : "";
                        otherSticker.setText(display);

                        // when user receives new messages from other users, vibrate
                        if (!user.username.equalsIgnoreCase(username)&&vibrator !=null && vibrator.hasVibrator()){
                            VibrationEffect effect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE);
                            vibrator.vibrate(effect);
                        } else {
                            Log.e(TAG, "No vibrator");
                        }
                        chatHistory.add(display);
                        mAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(mAdapter.getItemCount()-1);


                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled:" + databaseError);
                    }
                }
        );

        stickerView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);
                Log.d(TAG, selectedItem);
                String key = "";
                for (Map.Entry<String, String> entry : signOnUser.userSticker.defaultStickerGroup.entrySet()) {
                    if (entry.getValue().equals(selectedItem)) {
                        key=entry.getKey();
                    }
                }

                // send and display
                MainActivity.this.onSendSticker(mDatabase, key);
            }
        });
    }


    class MyAdapter extends RecyclerView.Adapter<MainActivity.ViewHolder> {

        private Context context;
        private List<String> list;

        public MyAdapter(Context context, List<String> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: " + list.size());
            Log.d(TAG, "Item is " + list.get(position));
            holder.vText.setText(list.get(position));
        }


        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView vText;
        public ViewHolder(View itemView) {
            super(itemView);
            vText = (TextView) itemView.findViewById(R.id.historyItem);
        }
    }





    public void sendMessageToNews(View type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToNews();
            }
        }).start();
    }

    private void sendMessageToNews(){
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("message", "This is a Firebase Cloud Messaging topic \"news\" message!");
            jNotification.put("body", "News Body");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");

            // Populate the Payload object.
            // Note that "to" is a topic, not a token representing an app instance
            jPayload.put("to", "/topics/news");
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

            // Open the HTTP connection and send the payload
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: " + resp);
                    Toast.makeText(MainActivity.this,"response was: " + resp,Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException | IOException e) {
            Log.e(TAG,"sendMessageToNews threw error",e);
        }
    }


    public void getHistory(String username) {
        // parse current sign on user stickers history to showHistory page
        Intent showHistoryIntent = new Intent(MainActivity.this, ShowHistory.class);
        showHistoryIntent.putExtra("showHistoryParse", signOnUser.history);
        showHistoryIntent.putExtra("username", username);

        startActivity(showHistoryIntent);
    }


    /**
     * Called on score add
     * @param postRef
     */
    // --> tap stickers and send
    private void onSendSticker(DatabaseReference postRef, final String stickerID) {
        postRef
                .child("users")
                .child(username)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        User u = mutableData.getValue(User.class);

                        // add to history
                        if (stickerID == null) Log.d(TAG, "input string empty");
                        u.history.add(u.userSticker.defaultStickerGroup.get(stickerID));

                        // display sticker
                        String display = username + ": " + u.history.get(u.history.size() - 1);
                        otherSticker.setText(display);


                        // display number of stickers sent
                        displayNum.setText("Total number of stickers sent: "+String.valueOf(u.history.size()));

                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // transaction completed
                        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                    }
                });
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }



}