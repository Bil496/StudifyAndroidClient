package com.bil496.studifyapp.realm;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;

import com.bil496.studifyapp.model.ChatMessage;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by burak on 3/20/2018.
 */

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from ChatMessage.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(ChatMessage.class);
        realm.commitTransaction();
    }

    //inserts the chat message object to database
    public void addMessage(ChatMessage chatMessage) {

        realm.beginTransaction();
        realm.copyToRealm(chatMessage);
        realm.commitTransaction();
    }

    //find all objects in the ChatMessage.class
    public RealmResults<ChatMessage> getMessages() {
        return realm.where(ChatMessage.class).findAll();
    }

    //check if ChatMessage.class is empty
    public boolean hasMessage() {

        return !realm.allObjects(ChatMessage.class).isEmpty();
    }
}