package com.bil496.studifyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.bil496.studifyapp.holder.TeamViewHolder;
import com.bil496.studifyapp.holder.UserAtTeamViewHolder;
import com.bil496.studifyapp.holder.UserViewHolder;
import com.bil496.studifyapp.model.Team;
import com.bil496.studifyapp.model.User;
import com.bil496.studifyapp.util.SharedPref;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    boolean isNotificationOn = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        ButterKnife.bind(this);
        chatBtn.setOnClickListener(this);
        notificationsBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
        requestsBtn.setOnClickListener(this);

        // SILINECEK
        Team team = (Team) getIntent().getSerializableExtra("team");
        createTreeView(team.getUsers());
        //SILINECEK
//        Integer teamId = SharedPref.read(SharedPref.CURRENT_TEAM_ID, -1);
//        if(teamId == -1)
//            finish();


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
                                //TODO: send quit request to the server.
                                finish();
                            }
                        })
                        .show();
                break;
            case R.id.fab_action_show_requests:
                break;
            default:
                break;
        }
    }
}
