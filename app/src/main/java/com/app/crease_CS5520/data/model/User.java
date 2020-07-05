package com.app.crease_CS5520.data.model;
import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class User {
    public String username;
    public ArrayList<String> history;
    public Stickers userSticker;

    public User(){
        history = new ArrayList<>();
        userSticker = new Stickers();
    }

    public User(String username){
        this.username = username;
        this.history = new ArrayList<>();
        userSticker = new Stickers();
    }

}
