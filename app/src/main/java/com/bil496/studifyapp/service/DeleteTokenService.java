package com.bil496.studifyapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.util.SharedPref;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by burak on 3/19/2018.
 */

public class DeleteTokenService extends IntentService {
    public static final String TAG = DeleteTokenService.class.getSimpleName();

    public DeleteTokenService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Check for current token
        String originalToken = SharedPref.read(SharedPref.FIREBASE_TOKEN, "");
        Log.d(TAG, "Token before deletion: " + originalToken);

        if (originalToken == "") {
            // Now manually call onTokenRefresh()
            Log.d(TAG, "Getting new token");
            FirebaseInstanceId.getInstance().getToken();
        } else {
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            RequestBody body =
                    RequestBody.create(MediaType.parse("text/plain"), originalToken);
            Call<ResponseBody> call = apiService.saveToken(SharedPref.read(SharedPref.USER_ID, -1), body);
            try {
                call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
