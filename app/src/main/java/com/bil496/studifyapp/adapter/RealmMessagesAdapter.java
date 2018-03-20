package com.bil496.studifyapp.adapter;

import android.content.Context;

import com.bil496.studifyapp.model.ChatMessage;

import io.realm.RealmResults;

/**
 * Created by burak on 3/20/2018.
 */

public class RealmMessagesAdapter extends RealmModelAdapter<ChatMessage> {

    public RealmMessagesAdapter(Context context, RealmResults<ChatMessage> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}