package com.bil496.studifyapp;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bil496.studifyapp.model.User;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.michelelacorte.scrollableappbar.ScrollableAppBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by burak on 3/16/2018.
 */

public class ProfileActivity extends AppCompatActivity{
    @BindView(R.id.profile_pp)
    ImageView profielPicView;
    @BindView(R.id.profile_current_team) TextView currentTeamView;
    @BindView(R.id.profile_current_topic) TextView currentTopicView;
    @BindView(R.id.profile_current_topic_layout)
    LinearLayout currentTopicLayout;
    @BindView(R.id.profile_current_team_layout)
    LinearLayout currentTeamLayout;

    @BindView(R.id.appbar)
    ScrollableAppBar appBarLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        User user = (User) getIntent().getSerializableExtra("user");
        setTitle(user.getName());

        Picasso.get()
                .load(user.getProfilePic())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(profielPicView);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<User> call = apiService.getUser(user.getUsername());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User>call, Response<User> response) {
                User user = response.body();
                if(user.getCurrentTeam() != null){
                    currentTeamView.setText(user.getCurrentTeam().getName());
                    currentTeamLayout.setVisibility(View.VISIBLE);
                }
                if(user.getCurrentTopic() != null){
                    currentTopicView.setText(user.getCurrentTopic().getTitle());
                    currentTopicLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
