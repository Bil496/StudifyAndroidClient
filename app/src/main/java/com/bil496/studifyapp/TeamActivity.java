package com.bil496.studifyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

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

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
                break;
            case R.id.fab_action_show_requests:
                break;
            default:
                break;
        }
    }
}
