package com.bil496.studifyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bil496.studifyapp.holder.TeamViewHolder;
import com.bil496.studifyapp.holder.UserAtTeamViewHolder;
import com.bil496.studifyapp.holder.UserViewHolder;
import com.bil496.studifyapp.model.Team;
import com.bil496.studifyapp.model.User;
import com.bil496.studifyapp.rest.APIError;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.rest.ErrorUtils;
import com.bil496.studifyapp.util.SharedPref;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by burak on 3/16/2018.
 */

public class TeamActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.fab_action_chat)
    FloatingActionButton chatBtn;
    @BindView(R.id.fab_action_notifications)
    FloatingActionButton notificationsBtn;
    @BindView(R.id.fab_action_quit_team)
    FloatingActionButton quitBtn;
    @BindView(R.id.fab_action_show_requests)
    FloatingActionButton requestsBtn;
    @BindView(R.id.container_layout)
    RelativeLayout relativeLayout;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.fab_action_locker)
    FloatingActionButton lockerBtn;

    boolean isNotificationOn = true;

    private Team team;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        ButterKnife.bind(this);
        chatBtn.setOnClickListener(this);
        notificationsBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
        requestsBtn.setOnClickListener(this);
        lockerBtn.setOnClickListener(this);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        loadData();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

    }

    private void updateViews(){
        SharedPref.write(SharedPref.CURRENT_TEAM_ID, team.getId());
        setTitle(team.getName());
        createTreeView(team.getUsers());
        refreshLayout.setRefreshing(false);
        if(team.getLocked()){
            lockerBtn.setIcon(R.drawable.ic_action_lock_closed);
            lockerBtn.setColorNormalResId(R.color.grayish_btn);
            lockerBtn.setColorPressedResId(R.color.grayish_btn_pressed);
        }else{
            lockerBtn.setIcon(R.drawable.ic_action_lock_open);
            lockerBtn.setColorNormalResId(R.color.green_btn);
            lockerBtn.setColorPressedResId(R.color.green_btn_pressed);
        }
    }

    public void removeUserFromTeam(final Integer kickedUserId){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiService.kickUser(SharedPref.read(SharedPref.USER_ID, -1),
                team.getId(), kickedUserId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    if(kickedUserId.equals(SharedPref.read(SharedPref.USER_ID, -1))){
                        SharedPref.write(SharedPref.CURRENT_TEAM_ID, -1);
                        // TODO: clear chat db
                        finish();
                    }
                    loadData();
                }else{
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(TeamActivity.this, error.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(TeamActivity.this, "FAILED", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadData(){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<Team> call = apiService.getTeam(SharedPref.read(SharedPref.USER_ID, -1));
        call.enqueue(new Callback<Team>() {
            @Override
            public void onResponse(Call<Team> call, Response<Team> response) {
                team = response.body();
                updateViews();
            }

            @Override
            public void onFailure(Call<Team> call, Throwable t) {
                Toast.makeText(TeamActivity.this, "Something did go wrong while getting team info.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void createTreeView(List<User> users){
        TreeNode root = TreeNode.root();

        for (User user : users){
            TreeNode userNode = new TreeNode(new UserViewHolder.UserItem(user)).setViewHolder(new UserAtTeamViewHolder(this));
            root.addChild(userNode);
        }
        relativeLayout.removeAllViews();
        relativeLayout.addView(new AndroidTreeView(this, root).getView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.team_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_share_team:
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_action_chat:
                break;
            case R.id.fab_action_notifications:
                isNotificationOn = !isNotificationOn;
                if(isNotificationOn){
                    notificationsBtn.setIcon(R.drawable.ic_action_volume_up);
                    notificationsBtn.setColorNormalResId(R.color.blue_btn);
                    notificationsBtn.setColorPressedResId(R.color.blue_btn_pressed);
                }else{
                    notificationsBtn.setIcon(R.drawable.ic_action_volume_mute);
                    notificationsBtn.setColorNormalResId(R.color.grayish_btn);
                    notificationsBtn.setColorPressedResId(R.color.grayish_btn_pressed);
                }
                break;
            case R.id.fab_action_quit_team:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to read older messages even if you come back!")
                        .setConfirmText("Yes, leave it!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                removeUserFromTeam(SharedPref.read(SharedPref.USER_ID, -1));
                                finish();
                            }
                        })
                        .show();
                break;
            case R.id.fab_action_show_requests:
                break;
            case R.id.fab_action_locker:
                if(team.getLocked()){
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Anyone be able to send join request!")
                            .setConfirmText("Yes, unlock it!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    //TODO: send unlock request to the server.
                                }
                            })
                            .show();
                }else{
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Won't be able to get requests from others!")
                            .setConfirmText("Yes, lock it!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    //TODO: send lock request to the server.
                                }
                            })
                            .show();
                }
                break;
            default:
                break;
        }
    }
}
