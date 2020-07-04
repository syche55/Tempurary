package com.app.crease_CS5520;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.crease_CS5520.data.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DatabaseReference mDatabase;
    private TextView otherSticker;
    private Button sendSticker;
    private Button getHistory;
    private TextView displayNum;
    private EditText enterSticker;
    private String username;
    private User signOnUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init sticker view
        otherSticker = (TextView) findViewById(R.id.otherSticker);
        enterSticker = (EditText) findViewById(R.id.enterSticker);
        // init database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // init stickers number display
        displayNum = (TextView) findViewById(R.id.displayNum);

        // get the username from login page
        Intent mIntent = getIntent();
        username = mIntent.getExtras().getString("username");


        // new user created in database
        signOnUser = new User(username);
        mDatabase.child("users").child(username).setValue(signOnUser);


        // send sticker button
        sendSticker = (Button)findViewById(R.id.sendSticker);
        sendSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.onSendSticker(mDatabase, enterSticker.getText().toString());
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
                        String display = user.username + ": " + user.history.get(user.history.size() - 1);
                        otherSticker.setText(display);
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
                        else if (stickerID.equals("1")) u.history.add(u.stickers.get(0));
                        else if (stickerID.equals("2")) u.history.add(u.stickers.get(1));
                        else u.history.add(u.stickers.get(2));

                        // display sticker
                        String display = username + ": " + u.history.get(u.history.size() - 1);
                        otherSticker.setText(display);

                        // display number of stickers sent
                        displayNum.setText(String.valueOf(u.history.size()));

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






}