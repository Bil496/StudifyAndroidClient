package com.bil496.studifyapp.service;

import android.util.Log;

import com.bil496.studifyapp.model.Place;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.util.SharedPref;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by burak on 3/16/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        saveTokenToSharedPref(refreshedToken);
    }

    private void saveTokenToSharedPref(String refreshedToken){
        SharedPref.init(getApplicationContext());
        SharedPref.write(SharedPref.FIREBASE_TOKEN, refreshedToken);
        if (SharedPref.read(SharedPref.USER_ID, -1) != -1){
            sendRegistrationToServer(SharedPref.read(SharedPref.USER_ID, -1), refreshedToken);
        }else{
            Log.w(TAG, "Token is not saved to the server because userId is not set yet.");
        }
    }

    private void sendRegistrationToServer(Integer userId, String refreshedToken){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        RequestBody body =
                RequestBody.create(MediaType.parse("text/plain"), refreshedToken);
        Call<ResponseBody> call = apiService.saveToken(userId, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "Token saved to the server successfully");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Problem occured while saving the token to the server");
            }
        });
    }
}
