package com.bil496.studifyapp.model;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by burak on 3/20/2018.
 */

public class ChatMessage extends RealmObject implements Serializable {
    @PrimaryKey
    private int id;

    private boolean isMine;
    private String content, senderName, senderImage;

    public ChatMessage() {
    }

    public ChatMessage(String content) {
        this.isMine = true;
        this.content = content;
    }

    public ChatMessage(String content, String senderName, String senderImage) {
        this.isMine = false;
        this.content = content;
        this.senderName = senderName;
        this.senderImage = senderImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }
}
