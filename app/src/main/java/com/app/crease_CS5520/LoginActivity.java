package com.app.crease_CS5520;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText typeUsername;
    private Button login;
    private String username;


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
}