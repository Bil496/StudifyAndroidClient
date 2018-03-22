package com.bil496.studifyapp.model;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by burak on 3/19/2018.
 */

public class Payload implements Serializable {
    private static Gson gson = new Gson();
    public enum Type {
        JOIN_REQUEST, ACCEPTED, DENIED, KICKED,
        // not supported yet
        FOLLOWED, CHAT_MESSAGE
    }
    private Type type;
    private Serializable payloadData;
    private Notification notification;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public <T> T getPayloadData(Class<T> classOfT) {
        return (T) payloadData;
    }

    public <T> void setPayloadData(String json, Class<T> classOfT) {
        this.payloadData = (Serializable) gson.fromJson(json, classOfT);
    }

    public void setPayloadData(Serializable payloadData) {
        this.payloadData = payloadData;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
