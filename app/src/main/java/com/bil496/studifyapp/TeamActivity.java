package com.bil496.studifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bil496.studifyapp.holder.UserAtTeamViewHolder;
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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by burak on 3/16/2018.
 */

public class TeamActivity extends AbstractObservableActivity implements View.OnClickListener {
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
        final int requestId = getIntent().getIntExtra("requestId", -1);
        if (requestId != -1) {
            Notification notification = (Notification) getIntent().getSerializableExtra("request");
            final ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText(notification.getTitle())
                    .setContentText(notification.getMessage())
                    .setCancelText("Deny")
                    .setConfirmText("Accept")
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Call<ResponseBody> call = apiService.postDenyRequest(SharedPref.read(SharedPref.USER_ID, -1), requestId);
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(TeamActivity.this, "Successfully denied", Toast.LENGTH_SHORT).show();
                                    } else {
                                        APIError error = ErrorUtils.parseError(response);
                                        Toast.makeText(TeamActivity.this, error.message(), Toast.LENGTH_LONG).show();
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
                            Call<ResponseBody> call = apiService.postAcceptRequest(SharedPref.read(SharedPref.USER_ID, -1), requestId);
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(TeamActivity.this, "Successfully accepted", Toast.LENGTH_SHORT).show();
                                        loadData();
                                    } else {
                                        APIError error = ErrorUtils.parseError(response);
                                        Toast.makeText(TeamActivity.this, error.message(), Toast.LENGTH_LONG).show();
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

    private void updateViews() {
        Status.whenEnterTopic(getBaseContext(), team.getTopic().getId());
        Status.whenEnterTeam(TeamActivity.this, team.getId());
        setTitle(team.getName());
        createTreeView(team.getUsers());
        refreshLayout.setRefreshing(false);
        if (team.getLocked()) {
            lockerBtn.setIcon(R.drawable.ic_action_lock_closed);
            lockerBtn.setColorNormalResId(R.color.grayish_btn);
            lockerBtn.setColorPressedResId(R.color.grayish_btn_pressed);
        } else {
            lockerBtn.setIcon(R.drawable.ic_action_lock_open);
            lockerBtn.setColorNormalResId(R.color.green_btn);
            lockerBtn.setColorPressedResId(R.color.green_btn_pressed);
        }
    }

    private void lockTeam(final boolean lock) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = null;
        if (lock) {
            call = apiService.lockTeam(SharedPref.read(SharedPref.USER_ID, -1),
                    team.getId());
        } else {
            call = apiService.unlockTeam(SharedPref.read(SharedPref.USER_ID, -1),
                    team.getId());
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TeamActivity.this, "Team is successfuly " + (lock ? "locked" : "unlocked"), Toast.LENGTH_LONG).show();
                    loadData();
                } else {
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

    public void removeUserFromTeam(final Integer kickedUserId) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiService.kickUser(SharedPref.read(SharedPref.USER_ID, -1),
                team.getId(), kickedUserId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (kickedUserId.equals(SharedPref.read(SharedPref.USER_ID, -1))) {
                        Status.whenQuitTeam(getBaseContext());
                        finish();
                    }
                    loadData();
                } else {
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

    protected void loadData() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<Team> call = apiService.getTeam(SharedPref.read(SharedPref.USER_ID, -1));
        call.enqueue(new Callback<Team>() {
            @Override
            public void onResponse(Call<Team> call, Response<Team> response) {
                if (response.isSuccessful()) {
                    team = response.body();
                    updateViews();
                } else {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Team> call, Throwable t) {
                Toast.makeText(TeamActivity.this, "Something did go wrong while getting team info.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void createTreeView(List<User> users) {
        TreeNode root = TreeNode.root();

        for (User user : users) {
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
        switch (v.getId()) {
            case R.id.fab_action_chat:
                startActivity(new Intent(this, ChatActivity.class));
                break;
            case R.id.fab_action_notifications:
                isNotificationOn = !isNotificationOn;
                if (isNotificationOn) {
                    notificationsBtn.setIcon(R.drawable.ic_action_volume_up);
                    notificationsBtn.setColorNormalResId(R.color.blue_btn);
                    notificationsBtn.setColorPressedResId(R.color.blue_btn_pressed);
                } else {
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
                if (team.getRequests().isEmpty()) {
                    Toast.makeText(TeamActivity.this, "There is no pending request", Toast.LENGTH_SHORT).show();
                } else {
                    String names[] = new String[team.getRequests().size()];
                    for (int i = 0; i < team.getRequests().size(); i++) {
                        names[i] = team.getRequests().get(i).getUser().getName();
                    }
                    new MaterialDialog.Builder(this)
                            .title("Pending requests")
                            .items(names)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, final int which, CharSequence text) {
                                    final ApiInterface apiService =
                                            ApiClient.getClient().create(ApiInterface.class);
                                    new SweetAlertDialog(TeamActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                            .setTitleText(text.toString())
                                            .setContentText(text.toString() + " wants to join your team")
                                            .setCancelText("Deny")
                                            .setConfirmText("Accept")
                                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    Call<ResponseBody> call = apiService.postDenyRequest(SharedPref.read(SharedPref.USER_ID, -1), team.getRequests().get(which).getId());
                                                    call.enqueue(new Callback<ResponseBody>() {
                                                        @Override
                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                            if (response.isSuccessful()) {
                                                                Toast.makeText(TeamActivity.this, "Successfully denied", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                APIError error = ErrorUtils.parseError(response);
                                                                Toast.makeText(TeamActivity.this, error.message(), Toast.LENGTH_LONG).show();
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
                                                    Call<ResponseBody> call = apiService.postAcceptRequest(SharedPref.read(SharedPref.USER_ID, -1), team.getRequests().get(which).getId());
                                                    call.enqueue(new Callback<ResponseBody>() {
                                                        @Override
                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                            if (response.isSuccessful()) {
                                                                Toast.makeText(TeamActivity.this, "Successfully accepted", Toast.LENGTH_SHORT).show();
                                                                loadData();
                                                            } else {
                                                                APIError error = ErrorUtils.parseError(response);
                                                                Toast.makeText(TeamActivity.this, error.message(), Toast.LENGTH_LONG).show();
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
                            })
                            .show();
                }
                break;
            case R.id.fab_action_locker:
                if (team.getLocked()) {
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Anyone be able to send join request!")
                            .setConfirmText("Yes, unlock it!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    lockTeam(false);
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } else {
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Won't be able to get requests from others!")
                            .setConfirmText("Yes, lock it!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    lockTeam(true);
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onNotification(Payload payload) {
        super.onNotification(payload);
        switch (payload.getType()) {
            case KICKED:
                if (payload.getPayloadData(Team.class).getId().equals(team.getId()))
                    finish();
                break;
        }
    }
}
