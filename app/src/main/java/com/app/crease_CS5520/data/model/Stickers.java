package com.app.crease_CS5520.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Stickers {
    public Map<String, String> defaultStickerGroup;

    public Stickers(){
        this.defaultStickerGroup = new HashMap<>();
        defaultStickerGroup.put("Heart", new String(Character.toChars(0x2764)));
        defaultStickerGroup.put("Tick", new String(Character.toChars(0x2705)));
        defaultStickerGroup.put("HighFive", new String(Character.toChars(0x270B)));
        defaultStickerGroup.put("Yeah", new String(Character.toChars(0x270C)));
        defaultStickerGroup.put("Star", new String(Character.toChars(0x2728)));
    }
}
