package com.bil496.studifyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.infideap.stylishwidget.view.Stylish;
import com.bil496.studifyapp.adapter.CustomItemClickListener;
import com.bil496.studifyapp.adapter.TopicAdapter;
import com.bil496.studifyapp.fragment.EnrollDialog;
import com.bil496.studifyapp.model.Topic;
import com.bil496.studifyapp.rest.APIError;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.rest.ErrorUtils;
import com.bil496.studifyapp.service.DeleteTokenService;
import com.bil496.studifyapp.service.UpdateCurrentInfoService;
import com.bil496.studifyapp.util.SharedPref;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by burak on 3/11/2018.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private Call<Topic[]> call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initial this before setContentView or declare in onCreate() of Custom Application
        String fontFolder = "Exo_2/Exo2-";
        Stylish.getInstance().set(
                fontFolder.concat("Regular.ttf"),
                fontFolder.concat("Bold.ttf"),
                fontFolder.concat("Italic.ttf"),
                fontFolder.concat("BoldItalic.ttf"));
        startService(new Intent(this, DeleteTokenService.class));
        startService(new Intent(this, UpdateCurrentInfoService.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SharedPref.init(getBaseContext());
        Integer userId = SharedPref.read(SharedPref.USER_ID, 0);
        Integer placeId = SharedPref.read(SharedPref.LOCATION_ID, 0);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        call = apiService.getTopics(userId, placeId);
        loadData();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        ab.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getBaseContext(), TopicFormActivity.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.action_refresh:
                loadData();
                return true;
            case R.id.action_change_place:
                SharedPref.write(SharedPref.IS_FIRST, true);
                Intent intent2 = new Intent(this, IntroActivity.class);
                startActivity(intent2);
                finish();
                return true;
            case R.id.action_exit:
                SharedPref.write(SharedPref.USER_ID, -1);
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                finish();
                return true;
            case R.id.action_reset:
                SharedPref.clear();
                startActivity(new Intent(this, IntroActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadData(){
        call.clone().enqueue(new Callback<Topic[]>() {
            @Override
            public void onResponse(Call<Topic[]>call, Response<Topic[]> response) {
                final List<Topic> topics = new ArrayList<Topic>(Arrays.asList(response.body()));
                recyclerView.setAdapter(new TopicAdapter(topics, R.layout.list_item_topic, getApplicationContext(), new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        final Topic topic = topics.get(position);
                        if(topic.getId().equals(SharedPref.read(SharedPref.CURRENT_TOPIC_ID, -1)) == false){
                            if (SharedPref.read(SharedPref.CURRENT_TEAM_ID, -1) != -1){
                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("You are currently in a team?")
                                        .setContentText("You should quit your team!")
                                        .setCancelText("No, I <3 them")
                                        .setConfirmText("Yes, quit me!")
                                        .showCancelButton(true)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                // TODO: send quit team request
                                                FragmentManager fm = getSupportFragmentManager();
                                                EnrollDialog custom = new EnrollDialog();
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("topic", topic);
                                                custom.setArguments(bundle);
                                                custom.show(fm,"");
                                            }
                                        })
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
                            }else{
                                FragmentManager fm = getSupportFragmentManager();
                                EnrollDialog custom = new EnrollDialog();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("topic", topic);
                                custom.setArguments(bundle);
                                custom.show(fm,"");
                            }
                        }else{
                            Toast.makeText(getBaseContext(), "You already enrolled", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getBaseContext(), TopicActivity.class);
                            intent.putExtra("topicId", topic.getId());
                            intent.putExtra("topicName", topic.getTitle());
                            startActivity(intent);
                        }
                    }
                }));

                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Topic[]>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                final Topic topic = (Topic) data.getSerializableExtra("topic");
                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<Integer> call2 = apiService.postTopic(SharedPref.read(SharedPref.LOCATION_ID, 0), topic);
                call2.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer>call, Response<Integer> response) {
                        if(response.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Topic " + topic.getTitle() + " created!", Toast.LENGTH_LONG).show();
                            loadData();
                        }else{
                            APIError error = ErrorUtils.parseError(response);
                            // … and use it to show error information
                            Toast.makeText(getApplicationContext(), error.message(), Toast.LENGTH_LONG).show();
                            // … or just log the issue like we’re doing :)
                            Log.d("error message", error.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer>call, Throwable t) {
                        // Log error here since request failed
                        Log.e(TAG, t.toString());
                    }
                });
            }
        }
    }
}
