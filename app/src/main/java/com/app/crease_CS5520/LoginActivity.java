package com.app.crease_CS5520;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText typeUsername;
    private Button login;
    private String username;
    // don't have to request the Push Notification permissions.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }
    // activity when clicking "login" button
    public void clickLogin(View view) {
        typeUsername = (EditText) findViewById(R.id.typeUsername);
        login = (Button) findViewById(R.id.login);
        username = typeUsername.getText().toString();

        // parse login username to MainActivity
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        myIntent.putExtra("username", username);
        startActivity(myIntent);
    }



    // save the pressed info somewhere, and restore the information back to achieve this.
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("usernameHolder", typeUsername.getText().toString());
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        typeUsername.setText(savedInstanceState.getString("usernameHolder"));
    }

}