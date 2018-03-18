package com.bil496.studifyapp.service;

import android.app.Activity;
import android.app.Notification;
import android.os.Bundle;
import android.util.Log;

import com.bil496.studifyapp.IntroActivity;
import com.bil496.studifyapp.R;
import com.bil496.studifyapp.model.PayloadType;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import br.com.goncalves.pugnotification.notification.Load;
import br.com.goncalves.pugnotification.notification.PugNotification;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // If an activity should start when clicking on the notification, set by payload type.
        Class<?> activityToStart = null;
        Bundle bundleOfActivity = null;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            PayloadType payloadType = PayloadType.valueOf(remoteMessage.getData().get("type"));
            String jsonString = remoteMessage.getData().get("payload");
            switch (payloadType){
                case JOIN_REQUEST:
                    break;
                case ACCEPTED:
                    break;
                case DENIED:
                    break;
                case KICKED:
                    break;
                case FOLLOWED:
                    break;
                case CHAT_MESSAGE:
                    break;
                default:
                    Log.e(TAG, "Unknown payload type came.");
                    break;

            }
            // TODO: Send data to database or foreground
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Load tmpLoad = PugNotification.with(this)
                    .load()
                    .title(remoteMessage.getNotification().getTitle())
                    .message(remoteMessage.getNotification().getBody())
                    .bigTextStyle(remoteMessage.getNotification().getBody())
                    .smallIcon(R.drawable.ic_launcher)
                    .largeIcon(R.mipmap.ic_launcher)
                    .flags(Notification.DEFAULT_ALL);
            if (activityToStart != null){
                if (bundleOfActivity == null){
                    tmpLoad = tmpLoad.click(activityToStart);
                }else{
                    tmpLoad = tmpLoad.click(activityToStart, bundleOfActivity);
                }
            }
            tmpLoad.simple().build();
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
