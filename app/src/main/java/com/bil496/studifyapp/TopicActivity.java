package com.bil496.studifyapp;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bil496.studifyapp.holder.TeamViewHolder;
import com.bil496.studifyapp.holder.UserViewHolder;
import com.bil496.studifyapp.model.Team;
import com.bil496.studifyapp.model.User;
import com.bil496.studifyapp.rest.APIError;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.rest.ErrorUtils;
import com.bil496.studifyapp.util.SharedPref;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

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
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.container_layout)
    RelativeLayout relativeLayout;
    private Call<Team[]> call;

    Integer userId;
    Integer topicId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        ButterKnife.bind(this);
        userId = SharedPref.read(SharedPref.USER_ID, 0);
        String topicName = getIntent().getStringExtra("topicName");
        topicId = getIntent().getIntExtra("topicId", 0);
        SharedPref.write(SharedPref.CURRENT_TOPIC_ID, topicId);
        setTitle("Teams of " + topicName);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        call = apiService.getTeams(userId, topicId);
        loadData();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        createTreeView(new ArrayList<Team>());

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    private void createTreeView(List<Team> teams){
        TreeNode root = TreeNode.root();

        for (Team team : teams){
            TreeNode teamNode = new TreeNode(new TeamViewHolder.TeamItem(team)).setViewHolder(new TeamViewHolder(this)).setExpanded(true);
            for (User user : team.getUsers()){
                TreeNode userNode = new TreeNode(new UserViewHolder.UserItem(user)).setViewHolder(new UserViewHolder(this));
                teamNode.addChild(userNode);
            }
            root.addChild(teamNode);
        }
        relativeLayout.removeAllViews();
        relativeLayout.addView(new AndroidTreeView(this, root).getView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<Team[]> call2 = apiService.createTeam(userId, topicId);
                call2.enqueue(new Callback<Team[]>() {
                    @Override
                    public void onResponse(Call<Team[]> call, Response<Team[]> response) {
                        if(response.isSuccessful()){
                            final List<Team> teams = new ArrayList<Team>(Arrays.asList(response.body()));
                            createTreeView(teams);
                            Log.d(TAG, response.toString());
                            refreshLayout.setRefreshing(false);
                        }else{
                            APIError error = ErrorUtils.parseError(response);
                            // … and use it to show error information
                            Toast.makeText(getApplicationContext(), error.message(), Toast.LENGTH_LONG).show();
                            // … or just log the issue like we’re doing :)
                            Log.d("error message", error.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Team[]> call, Throwable t) {
                    }
                });
                return true;
            case R.id.action_edit:
                Toast.makeText(this, "Talents editing is not possible right now.", Toast.LENGTH_LONG).show();
                return true;
            case android.R.id.home:
                finish();
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
                createTreeView(teams);
                Log.d(TAG, response.toString());
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Team[]>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                refreshLayout.setRefreshing(false);
            }
        });
    }
}
