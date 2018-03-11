package com.bil496.studifyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bil496.studifyapp.adapter.CustomItemClickListener;
import com.bil496.studifyapp.adapter.TopicAdapter;
import com.bil496.studifyapp.fragment.EnrollDialog;
import com.bil496.studifyapp.model.Topic;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.util.SharedPref;

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
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fab) FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SharedPref.init(getBaseContext());
        Integer userId = SharedPref.read(SharedPref.USER_ID, 0);
        Integer placeId = SharedPref.read(SharedPref.LOCATION_ID, 0);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<Topic[]> call = apiService.getTopics(placeId);
        call.enqueue(new Callback<Topic[]>() {
            @Override
            public void onResponse(Call<Topic[]>call, Response<Topic[]> response) {
                final List<Topic> topics = new ArrayList<Topic>(Arrays.asList(response.body()));
                recyclerView.setAdapter(new TopicAdapter(topics, R.layout.list_item_topic, getApplicationContext(), new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Topic topic = topics.get(position);
                        Toast.makeText(getBaseContext(), topic.getTitle(), Toast.LENGTH_LONG).show();
                        FragmentManager fm = getSupportFragmentManager();
                        EnrollDialog custom = new EnrollDialog();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("topic", topic);
                        custom.setArguments(bundle);
                        custom.show(fm,"");
                    }
                }));
            }

            @Override
            public void onFailure(Call<Topic[]>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), TopicFormActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                Topic topic = (Topic) data.getSerializableExtra("topic");
                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<String> call = apiService.postTopic(SharedPref.read(SharedPref.LOCATION_ID, 0), topic);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String>call, Response<String> response) {
                        Toast.makeText(getBaseContext(), response.body(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<String>call, Throwable t) {
                        // Log error here since request failed
                        Log.e(TAG, t.toString());
                    }
                });
            }
        }
    }
}
