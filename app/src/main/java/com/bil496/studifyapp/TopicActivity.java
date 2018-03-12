package com.bil496.studifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bil496.studifyapp.adapter.CustomItemClickListener;
import com.bil496.studifyapp.adapter.TopicAdapter;
import com.bil496.studifyapp.fragment.EnrollDialog;
import com.bil496.studifyapp.model.Team;
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
 * Created by burak on 3/12/2018.
 */

public class TopicActivity extends AppCompatActivity {
    private static final String TAG = TopicActivity.class.getSimpleName();
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private Call<Team[]> call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Integer userId = SharedPref.read(SharedPref.USER_ID, 0);
        String topicName = getIntent().getStringExtra("topicName");
        Integer topicId = getIntent().getIntExtra("topicId", 0);
        setTitle("Teams of " + topicName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        call = apiService.getTeams(userId, topicId);
        loadData();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadData(){
        call.clone().enqueue(new Callback<Team[]>() {
            @Override
            public void onResponse(Call<Team[]>call, Response<Team[]> response) {
                final List<Team> teams = new ArrayList<Team>(Arrays.asList(response.body()));
                Log.d(TAG, response.toString());
            }

            @Override
            public void onFailure(Call<Team[]>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }
}
