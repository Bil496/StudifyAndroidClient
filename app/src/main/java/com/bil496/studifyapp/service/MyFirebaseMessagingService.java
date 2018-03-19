package com.bil496.studifyapp.service;

import android.app.Notification;
import android.os.Bundle;
import android.util.Log;

import com.bil496.studifyapp.R;
import com.bil496.studifyapp.TeamActivity;
import com.bil496.studifyapp.TopicActivity;
import com.bil496.studifyapp.model.Payload;
import com.bil496.studifyapp.model.Team;
import com.bil496.studifyapp.model.User;
import com.bil496.studifyapp.util.SharedPref;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sergiocasero.notifikationmanager.NotifikationManager;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.goncalves.pugnotification.notification.Load;
import br.com.goncalves.pugnotification.notification.PugNotification;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // If an activity should start when clicking on the notification, set by payload type.
        Class<?> activityToStart = null;
        Bundle bundleOfActivity = null;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Payload payload = new Payload();

            payload.setType(Payload.Type.valueOf(remoteMessage.getData().get("type")));
            payload.setNotification(remoteMessage.getNotification());
            String jsonString = remoteMessage.getData().get("payload");
            switch (payload.getType()){
                case JOIN_REQUEST:
                    try {
                        JSONObject obj = new JSONObject(jsonString);
                        payload.setPayloadData(new Integer(obj.getInt("id")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    activityToStart = TeamActivity.class;
                    bundleOfActivity = new Bundle();
                    bundleOfActivity.putSerializable("requestId", payload.getPayloadData(Integer.class));
                    bundleOfActivity.putSerializable("notification", payload.getNotification());
                    break;
                case ACCEPTED:
                    payload.setPayloadData(jsonString, Team.class);
                    activityToStart = TeamActivity.class;
                    SharedPref.write(SharedPref.CURRENT_TEAM_ID, payload.getPayloadData(Team.class).getId());
                    break;
                case DENIED:
                    // Payload comes null
                    break;
                case KICKED:
                    payload.setPayloadData(jsonString, Team.class);
                    activityToStart = TopicActivity.class;
                    SharedPref.write(SharedPref.CURRENT_TEAM_ID, -1);
                    bundleOfActivity = new Bundle();
                    bundleOfActivity.putStringArray("dialog", new String[]{
                            remoteMessage.getNotification().getTitle(),
                            remoteMessage.getNotification().getBody()}
                    );
                    break;
                case FOLLOWED:
                    // Not supported yet
                    break;
                case CHAT_MESSAGE:
                    try {
                        JSONObject obj = new JSONObject(jsonString);
                        payload.setPayloadData(obj.getString("chatMessage"), String.class);
                        // activityToStart = ChatActivity.class;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    Log.e(TAG, "Unknown payload type came.");
                    break;

            }
            NotifikationManager.INSTANCE.notify(payload);
            // TODO: Send data to database
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
