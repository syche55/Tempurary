package com.app.crease_CS5520;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.crease_CS5520.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String SERVER_KEY = "AAAA2hXlUTA:APA91bFhRbEipDF4z0LT4DJUC05x5hfL2yFXohyw4KyTQ0OVV349roAvnSPjiwGtrSUI-VO8b3IF55V6itrf-Y5FCtkAXHYq7dw066uqNA7C16UANOysccvY0SJfwrtW0x6LmAcx8UD8";
    private static final String CHANNEL_ID = "newMessage";
    private DatabaseReference mDatabase;
    private TextView otherSticker;
    private Button sendSticker;
    private Button getHistory;
    private TextView displayNum;
    private String username;
    private User signOnUser;
    private ListView stickerView;
    private ArrayAdapter<String> adapterStickers;
    private ArrayList<String> stickerContainer;
    private ArrayList<String> chatHistory;
    private RecyclerView.Adapter adapterChatHistory;
    private RecyclerView recyclerView;
    private Vibrator vibrator;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private ActivityManager activityManager;
    private String packageName;
    private boolean stop = false;
    private LinearLayoutManager layoutManager;

    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init stickers
        stickerContainer = new ArrayList<>();

        // init sticker view
        otherSticker = (TextView) findViewById(R.id.otherSticker);

        // init database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // init stickers number display
        displayNum = (TextView) findViewById(R.id.displayNum);

        // get the username from login page
        Intent mIntent = getIntent();
        username = mIntent.getExtras().getString("username");

        // retrieve user information
        DatabaseReference userNameRef = mDatabase.child("users").child(username);

        // check if user in the database
        ValueEventListener eventListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // if not in database, create new user
                    signOnUser = new User(username);
                    mDatabase.child("users").child(username).setValue(signOnUser);
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

                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                        return;
                                    }

                                    // Get new Instance ID token
                                    String token = task.getResult().getToken();
                                    Log.d(TAG, "token: " + token);
                                }
                            });

                    Log.d(TAG, "username=" + signOnUser.username);
                } else {
                    // if no user in db, create new user
                    User user = dataSnapshot.getValue(User.class);
                    signOnUser = user;
                    displayNum.setText("Total number of stickers sent: " + String.valueOf(user.history.size()));
                    Log.d(TAG, signOnUser.username);
                }
                stickerContainer.addAll(signOnUser.userSticker.defaultStickerGroup.values());
                adapterStickers.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);


        // create user sticker view, user chooses stickers from here
        stickerView = findViewById(R.id.stickerView);
        adapterStickers = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, stickerContainer);
        stickerView.setAdapter(adapterStickers);


        // create user chat history view, including other users' messages
        recyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
        if (savedInstanceState != null && savedInstanceState.getStringArrayList("data") != null) {
            chatHistory = savedInstanceState.getStringArrayList("data");
            Log.d(TAG, "load history from saved instance:" + chatHistory.toString());
        } else {
            chatHistory = new ArrayList<>();
            Log.d(TAG, "created new chat history ");

        }
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapterChatHistory = new MyAdapter(MainActivity.this, chatHistory);
        recyclerView.setAdapter(adapterChatHistory);

        // get history button
        getHistory = (Button) findViewById(R.id.getHistory);
        getHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHistory(username);
            }
        });

        // initiate vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // add users ChildEventListener
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // test if node added correctly
                Log.e(TAG, "onChildAdded: dataSnapshot = " + dataSnapshot.getValue());
            }
            
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "Call onChildChange of database");
                User user = dataSnapshot.getValue(User.class);
                // check if the change made is the login user
                if (user.username.equalsIgnoreCase(username)) {
                    signOnUser = user;
                }
                // display username and sticker
                String display = user.history.size() > 0 ? user.username + ": " + user.history.get(user.history.size() - 1) : "";
                otherSticker.setText(display);

                // when user receives new messages from other users, vibrate
                if (!user.username.equalsIgnoreCase(username) && vibrator != null && vibrator.hasVibrator()) {
                    VibrationEffect effect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE);
                    vibrator.vibrate(effect);
                } else {
                    Log.e(TAG, "No vibrator");
                }
                // when user receives new messages from other users and app in background
                if (!user.username.equalsIgnoreCase(username) && !appOnForeground()) {
                    // go to main page when clicking the notification
                    Context context = getApplicationContext();
                    Intent intent = new Intent(context, MainActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

                    PendingIntent callIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(),
                            new Intent(context, MainActivity.class), 0);

                    mBuilder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                            .setContentTitle("Crease New Message")
                            .setContentText(display)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                    //via builder.build() we get the notification
                    mNotificationManager.notify(1, mBuilder.build());
                }
                // update chat history
                chatHistory.add(display);
                Log.d(TAG, "add one display to chatHistory " + display);
                adapterChatHistory.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapterChatHistory.getItemCount() - 1);
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
        };
        mDatabase.child("users").addChildEventListener(childEventListener);

        // add OnItemClickListener for user tapping stickers
        stickerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "Sticker select: " + selectedItem);
                String key = "";
                for (Map.Entry<String, String> entry : signOnUser.userSticker.defaultStickerGroup.entrySet()) {
                    if (entry.getValue().equals(selectedItem)) {
                        key = entry.getKey();
                    }
                }
                // send to database and display
                MainActivity.this.onSendSticker(mDatabase, key);
            }
        });

        // notification manager for notification push
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        // activity manager for app running in background check
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        packageName = this.getPackageName();
        new Thread(new AppStatus()).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // remove event listener to avoid duplicate listener, especially for screen rotation
        mDatabase.child("users").removeEventListener(childEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    /**
     * Called on score add
     *
     * @param postRef
     */
    // --> tap stickers and send
    private void onSendSticker(final DatabaseReference postRef, final String stickerID) {
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

                        // send message to news subscribe users
                        sendMessageToNews(display);


                        // display number of stickers sent
                        displayNum.setText("Total number of stickers sent: " + String.valueOf(u.history.size()));

                        mutableData.setValue(u);
                        Log.d(TAG, "Sending sticker: " + display);
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

    // user message sent to database - news topic subscribe users
    private void sendMessageToNews(String newMessage) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("message", "This is a Firebase Cloud Messaging topic \"news\" message!");
            jNotification.put("body", newMessage);
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
            conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);
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
                }
            });
        } catch (JSONException | IOException e) {
            Log.e(TAG, "sendMessageToNews threw error", e);
        }
    }

    // save state
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "saving chatHistory" + chatHistory.toString());
        outState.putStringArrayList("data", chatHistory);
    }


    // user stickers history in new page
    public void getHistory(String username) {
        // parse current sign on user stickers history to showHistory page
        Intent showHistoryIntent = new Intent(MainActivity.this, ShowHistory.class);
        showHistoryIntent.putExtra("showHistoryParse", signOnUser.history);
        showHistoryIntent.putExtra("username", username);
        startActivity(showHistoryIntent);
    }

    // notification channel
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // RecyclerView adapter
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
            holder.vText.setText(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
    }

    // RecyclerView view holder
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView vText;

        public ViewHolder(View itemView) {
            super(itemView);
            vText = (TextView) itemView.findViewById(R.id.historyItem);
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }


    // app running in background status check
    private class AppStatus implements Runnable {
        @Override
        public void run() {
            stop = false;
            while (!stop) {
                try {
                    if (appOnForeground()) {
                        // do nothing
                    } else {
                        // do nothing
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean appOnForeground() {
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}