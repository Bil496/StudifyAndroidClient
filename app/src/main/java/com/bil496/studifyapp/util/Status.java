package com.bil496.studifyapp.util;

import android.content.Context;
import android.content.Intent;

import com.bil496.studifyapp.TeamActivity;
import com.bil496.studifyapp.model.ChatMessage;

import io.realm.Realm;

/**
 * Created by burak on 3/21/2018.
 */

public class Status {
    public static void whenQuitTeam(Context context){
        SharedPref.init(context);
        // Set current team null
        SharedPref.write(SharedPref.CURRENT_TEAM_ID, -1);
        SharedPref.write(SharedPref.UNREAD_COUNT, 0);
        // Clear chat history
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.clear(ChatMessage.class);
        realm.commitTransaction();
    }

    public static void whenEnterTeam(Context context, Integer teamId){
        SharedPref.init(context);
        if(SharedPref.read(SharedPref.CURRENT_TEAM_ID, -1).equals(teamId)){
            // Ignore
        }else{
            // Set current team id
            SharedPref.write(SharedPref.CURRENT_TEAM_ID, teamId);
            SharedPref.write(SharedPref.UNREAD_COUNT, 0);
            // Clear chat history
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.clear(ChatMessage.class);
            realm.commitTransaction();
            // Opens team activity
            context.startActivity(new Intent(context, TeamActivity.class));
        }
    }

    public static void whenQuitTopic(Context context){
        whenQuitTeam(context);
        // Set current topic null
        SharedPref.write(SharedPref.CURRENT_TOPIC_ID, -1);
    }

    public static void whenEnterTopic(Context context, Integer topicId){
        SharedPref.init(context);
        if(SharedPref.read(SharedPref.CURRENT_TOPIC_ID, -1).equals(topicId)){
            // Ignore
        }else{
            whenQuitTeam(context);
            // Set current topic id
            SharedPref.write(SharedPref.CURRENT_TOPIC_ID, topicId);
        }
    }
}
