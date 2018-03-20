package com.bil496.studifyapp.model;

import java.io.Serializable;

/**
 * Created by burak on 3/20/2018.
 */

public class ChatMessage implements Serializable {
    private boolean isMine;
    private String content, senderName, senderImage;

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
