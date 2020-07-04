package com.app.crease_CS5520;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ShowHistory extends AppCompatActivity {

    private ArrayList<String> showHistoryParse;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);

        Intent intent = getIntent();

        // get parsed in history data of current signon user
        showHistoryParse = (ArrayList<String>) intent.getSerializableExtra("showHistoryParse");

        listView = findViewById(R.id.stickersHistory);

        // parse history to ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, showHistoryParse);
        listView.setAdapter(adapter);

    }
}