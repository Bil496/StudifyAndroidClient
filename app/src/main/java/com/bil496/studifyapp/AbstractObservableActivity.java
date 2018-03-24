package com.bil496.studifyapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.bil496.studifyapp.model.Payload;
import com.bil496.studifyapp.rest.APIError;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.rest.ErrorUtils;
import com.bil496.studifyapp.util.SharedPref;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sergiocasero.notifikationmanager.NotifikationManager;

import java.util.Observable;
import java.util.Observer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by burak on 3/19/2018.
 */

public abstract class AbstractObservableActivity extends AppCompatActivity implements Observer {

    protected void onNotification(final Payload payload) {
        final Activity mActivity = AbstractObservableActivity.this;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (payload.getType()) {
                    case KICKED:
                    case DENIED:
                        new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(payload.getNotification().getTitle())
                                .setContentText(payload.getNotification().getMessage())
                                .show();
                        break;
                    case ACCEPTED:
                        Toast.makeText(mActivity, payload.getNotification().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mActivity, TeamActivity.class);
                        startActivity(intent);
                        break;
                    case JOIN_REQUEST:
                        final ApiInterface apiService =
                                ApiClient.getClient().create(ApiInterface.class);
                        new SweetAlertDialog(mActivity, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText(payload.getNotification().getTitle())
                                .setContentText(payload.getNotification().getMessage())
                                .setCancelText("Deny")
                                .setConfirmText("Accept")
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Call<ResponseBody> call = apiService.postDenyRequest(SharedPref.read(SharedPref.USER_ID, -1), payload.getPayloadData(Integer.class));
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(mActivity, "Successfully denied", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    APIError error = ErrorUtils.parseError(response);
                                                    Toast.makeText(mActivity, error.message(), Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            }
                                        });
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Call<ResponseBody> call = apiService.postAcceptRequest(SharedPref.read(SharedPref.USER_ID, -1), payload.getPayloadData(Integer.class));
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(mActivity, "Successfully accepted", Toast.LENGTH_SHORT).show();
                                                    if (mActivity instanceof TeamActivity) {
                                                        ((TeamActivity) mActivity).loadData();
                                                    } else {
                                                        Intent intent = new Intent(mActivity, TeamActivity.class);
                                                        startActivity(intent);
                                                    }
                                                } else {
                                                    APIError error = ErrorUtils.parseError(response);
                                                    Toast.makeText(mActivity, error.message(), Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            }
                                        });
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        NotifikationManager.INSTANCE.addObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NotifikationManager.INSTANCE.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        onNotification((Payload) arg);
    }
}
