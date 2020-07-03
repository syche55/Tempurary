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


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DatabaseReference mDatabase;
    private TextView curUser;
    private TextView otherUser;
    private TextView curSticker;
    private TextView otherSticker;
    private Button sendSticker;
    private Button getHistory;
    private TextView displayNum;
    private EditText enterSticker;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init users' name
        curUser = (TextView) findViewById(R.id.curUser);
        otherUser = (TextView) findViewById(R.id.otherUser);
        // init sticker view
        otherSticker = (TextView) findViewById(R.id.otherSticker);
        enterSticker = (EditText) findViewById(R.id.enterSticker);
        // init database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // init stickers number display
        displayNum = (TextView) findViewById(R.id.displayNum);

        // get the username
        Intent mIntent = getIntent();
        username = mIntent.getExtras().getString("username");

        Log.d(TAG, "Check if user name entered correctly"+username);


        // new user created in database
        User user = new User(username);
        mDatabase.child("users").child(username).setValue(user);


        sendSticker = (Button)findViewById(R.id.sendSticker);
        sendSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.onSendSticker(mDatabase, enterSticker.getText().toString());
            }
        });

        getHistory = (Button)findViewById(R.id.getHistory);
//        getHistory.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                getHistory();
//            }
//        });


        mDatabase.child("users").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                       // User user = dataSnapshot.getValue(User.class);

//                        if (dataSnapshot.getKey().equalsIgnoreCase("user1")) {
//                            score.setText(user.score);
//                            userName.setText(user.username);
//                        } else {
//                            score2.setText(String.valueOf(user.score));
//                            userName2.setText(user.username);
//                        }


                        Log.e(TAG, "onChildAdded: dataSnapshot = " + dataSnapshot.getValue());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        User user = dataSnapshot.getValue(User.class);

//                        otherSticker.setText(user.history.get(user.history.size() - 1));
//                        displayNum.setText(user.history.size());
                        Log.v(TAG, "onChildChanged: "+dataSnapshot.getValue().toString());
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

//    public void getHistory(DatabaseReference postRef, String user) {
//        startActivity(new Intent(MainActivity.this, ShowHistory.class));
//    }

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
                        //if (u == null) {

                            //Log.d(TAG,"USER LOG");
                            //return Transaction.success(mutableData);
                        //}

                        // add to history
                        if (stickerID == null) Log.d(TAG, "input string empty");
                        else if (stickerID.equals("1")) u.history.add(u.stickers.get(0));
                        else if (stickerID.equals("2")) u.history.add(u.stickers.get(1));
                        else u.history.add(u.stickers.get(2));

                        // display sticker
                        String display = username + ": " + u.history.get(u.history.size() - 1);
                        otherSticker.setText(display);

                        // display number
                        displayNum.setText(String.valueOf(u.history.size()));

                        mutableData.setValue(u);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                    }
                });
    }


    public void doAddDataToDb(View view){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");

        // Read from the database by listening for a change to that item.
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
//                TextView tv = (TextView)findViewById(R.id.dataUpdateTextView);
//                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }



}