package com.bil496.studifyapp.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bil496.studifyapp.R;
import com.bil496.studifyapp.TeamActivity;
import com.bil496.studifyapp.util.SharedPref;
import com.github.johnkil.print.PrintView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;
import com.unnamed.b.atv.model.TreeNode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by burak on 3/17/2018.
 */

public class UserAtTeamViewHolder extends TreeNode.BaseNodeViewHolder<UserViewHolder.UserItem> {
    @BindView(R.id.arrow_icon)
    PrintView arrowView;
    @BindView(R.id.profile_pic)
    ImageView profileImage;
    @BindView(R.id.name_of_user)
    TextView userNameLabel;
    @BindView(R.id.btn_kickUser)
    PrintView kickUserView;

    public UserAtTeamViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, final UserViewHolder.UserItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.node_user_of_team, null, false);
        ButterKnife.bind(this, view);
        Picasso.get()
                .load(value.user.getProfilePic())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(profileImage);
        userNameLabel.setText(value.user.getName());
        if (SharedPref.read(SharedPref.USER_ID, -1).equals(value.user.getId())) {
            kickUserView.setIconText(context.getResources().getString(R.string.ic_account_circle));
            kickUserView.setIconColor(R.color.green_btn_pressed);
        } else {
            kickUserView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText(value.user.getName() + " would get revenge!")
                            .setConfirmText("Yes, kick it!")
                            .setCancelText("Nevermind...")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    TeamActivity activity = (TeamActivity) context;
                                    activity.removeUserFromTeam(value.user.getId());
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
            });
        }
        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }
}
