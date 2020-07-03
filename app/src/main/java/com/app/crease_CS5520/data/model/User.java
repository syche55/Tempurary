package com.app.crease_CS5520.data.model;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.ArrayList;

public class User {
    public String username;
    public ArrayList<String> stickers;
    public ArrayList<String> history;

    public User(){
    }

    public User(String username){
        this.username = username;
        this.stickers = new ArrayList<>();
        stickers.add(new String(Character.toChars(0x2764)));
        stickers.add(new String(Character.toChars(0x2705)));
        stickers.add(new String(Character.toChars(0x270B)));
    }

}