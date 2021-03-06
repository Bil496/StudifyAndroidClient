package com.bil496.studifyapp;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bil496.studifyapp.holder.TeamViewHolder;
import com.bil496.studifyapp.holder.UserViewHolder;
import com.bil496.studifyapp.model.Notification;
import com.bil496.studifyapp.model.Payload;
import com.bil496.studifyapp.model.Team;
import com.bil496.studifyapp.model.User;
import com.bil496.studifyapp.rest.APIError;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.rest.ErrorUtils;
import com.bil496.studifyapp.util.SharedPref;
import com.bil496.studifyapp.util.Status;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
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

public class TopicActivity extends AbstractObservableActivity {
    private static final String TAG = TopicActivity.class.getSimpleName();
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.container_layout)
    ScrollView containerLayout;
    Integer userId;
    Integer topicId;
    private Call<Team[]> call;

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

        if (getIntent().hasExtra("kicked")) {
            Notification notification = (Notification) getIntent().getSerializableExtra("kicked");
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(notification.getTitle())
                    .setContentText(notification.getMessage())
                    .show();
        }
    }

    private void createTreeView(List<Team> teams) {
        TreeNode root = TreeNode.root();

        for (Team team : teams) {
            TreeNode teamNode = new TreeNode(new TeamViewHolder.TeamItem(team)).setViewHolder(new TeamViewHolder(this)).setExpanded(true);
            for (User user : team.getUsers()) {
                TreeNode userNode = new TreeNode(new UserViewHolder.UserItem(user)).setViewHolder(new UserViewHolder(this));
                teamNode.addChild(userNode);
            }
            root.addChild(teamNode);
        }
        containerLayout.removeAllViews();
        containerLayout.addView(new AndroidTreeView(this, root).getView());
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
                Call<Integer> call2 = apiService.createTeam(userId, topicId);
                call2.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.isSuccessful()) {
                            Status.whenEnterTeam(TopicActivity.this, response.body());
                            loadData();
                            refreshLayout.setRefreshing(false);
                        } else {
                            APIError error = ErrorUtils.parseError(response);
                            // … and use it to show error information
                            Toast.makeText(getApplicationContext(), error.message(), Toast.LENGTH_LONG).show();
                            // … or just log the issue like we’re doing :)
                            Log.d("error message", error.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
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

    private void loadData() {
        call.clone().enqueue(new Callback<Team[]>() {
            @Override
            public void onResponse(Call<Team[]> call, Response<Team[]> response) {
                if (response.isSuccessful()) {
                    final List<Team> teams = new ArrayList<Team>(Arrays.asList(response.body()));
                    createTreeView(teams);
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(TopicActivity.this, error.message(), Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, response.toString());
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Team[]> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onNotification(Payload payload) {
        super.onNotification(payload);
        switch (payload.getType()) {
            case KICKED:
            case DENIED:
                loadData();
                break;
        }
    }
}
