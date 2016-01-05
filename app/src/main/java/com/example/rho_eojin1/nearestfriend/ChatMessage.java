package com.example.rho_eojin1.nearestfriend;

/**
 * Created by Rho-Eojin1 on 2016. 1. 2..
 */
public class ChatMessage {
    private String name;
    private String text;

    public ChatMessage() {
        // necessary for Firebase's deserializer
    }
    public ChatMessage(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
