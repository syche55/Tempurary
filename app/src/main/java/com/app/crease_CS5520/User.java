package com.app.crease_CS5520;

import java.util.ArrayList;

public class User {
    public String username;
    public ArrayList<Integer> stickers;
    public ArrayList<Integer> history;

    public User(){
    }

    public User(String username){
        this.username = username;
        this.stickers = new ArrayList<>();
        stickers.add(0);

    }

}
