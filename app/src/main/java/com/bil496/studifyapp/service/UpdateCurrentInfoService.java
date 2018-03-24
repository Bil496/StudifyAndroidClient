package com.bil496.studifyapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.bil496.studifyapp.model.User;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.util.SharedPref;
import com.bil496.studifyapp.util.Status;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by burak on 3/19/2018.
 */

public class UpdateCurrentInfoService extends IntentService {
    public static final String TAG = DeleteTokenService.class.getSimpleName();

    public UpdateCurrentInfoService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<User> call = apiService.getUser(SharedPref.read(SharedPref.USERNAME, ""));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (user.getCurrentTopic() != null)
                    Status.whenEnterTopic(getBaseContext(), user.getCurrentTopic().getId());

                if (user.getCurrentTeam() != null)
                    Status.whenEnterTeam(UpdateCurrentInfoService.this, user.getCurrentTeam().getId());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }
}
