package com.bil496.studifyapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by burak on 3/11/2018.
 */

public class SharedPref {
    private static SharedPreferences mSharedPref;
    public static final String LOCATION_ID = "LOCATION_ID";
    public static final String LOCATION_TITLE = "LOCATION_TITLE";
    public static final String USER_ID = "USER_ID";
    public static final String IS_FIRST = "IS_FIRST";
    public static final String CURRENT_TOPIC_ID = "CURRENT_TOPIC_ID";
    public static final String CURRENT_TEAM_ID = "CURRENT_TEAM_ID";
    public static final String FIREBASE_TOKEN = "FIREBASE_TOKEN";

    private SharedPref()
    {

    }

    public static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).commit();
    }

    public static void clear(){
        String firebaseToken = SharedPref.read(SharedPref.FIREBASE_TOKEN, "");
        mSharedPref.edit().clear().commit();
        if(firebaseToken.length() > 0)
            SharedPref.write(SharedPref.FIREBASE_TOKEN, firebaseToken);
    }
}
