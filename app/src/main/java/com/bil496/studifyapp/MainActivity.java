package com.bil496.studifyapp;

import android.os.Bundle;
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
    private static final String TAG = IntroActivity.class.getSimpleName();
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
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
                        // TODO: topic creation
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
    }
}
