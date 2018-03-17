package com.bil496.studifyapp.holder;

/**
 * Created by burak on 3/12/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bil496.studifyapp.R;
import com.bil496.studifyapp.TeamActivity;
import com.bil496.studifyapp.model.Team;
import com.bil496.studifyapp.util.SharedPref;
import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeamViewHolder extends TreeNode.BaseNodeViewHolder<TeamViewHolder.TeamItem> {
    @BindView(R.id.arrow_icon) PrintView arrowView;
    @BindView(R.id.icon) PrintView iconView;
    @BindView(R.id.node_value) TextView teamNameLabel;
    @BindView(R.id.size) TextView sizeLabel;
    public TeamViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, final TeamItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.node_team, null, false);
        ButterKnife.bind(this, view);
        iconView.setIconText(context.getResources().getString(R.string.ic_people));
        teamNameLabel.setText(value.team.getName() + " ("+value.team.getUtilityScore()+")");
        sizeLabel.setText(value.team.getUsers().size() + (value.team.getUsers().size() == 1 ? " person" : " people "));
        final PrintView sendRequestView = view.findViewById(R.id.btn_sendRequest);
        if(value.team.getId() == SharedPref.read(SharedPref.CURRENT_TEAM_ID, -1)){
            sendRequestView.setIconText(R.string.ic_verified_user);
        }
        else if(value.team.getLocked()){
            sendRequestView.setIconText(R.string.ic_lock);
            sendRequestView.setIconColor(R.color.red);
        }else{
            sendRequestView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendRequestView.setIconText(R.string.ic_done);
                    sendRequestView.setClickable(false);
                    // TODO: Send request
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            sendRequestView.setIconText(R.string.ic_done_all);
                        }
                    }, 2000);
                }
            });
        }
        teamNameLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TeamActivity.class);
                context.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }


    public static class TeamItem {
        public Team team;

        public TeamItem(Team team) {
            this.team = team;
        }
        // rest will be hardcoded
    }

}
