package com.bil496.studifyapp.holder;

/**
 * Created by burak on 3/12/2018.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bil496.studifyapp.R;
import com.bil496.studifyapp.model.User;
import com.github.johnkil.print.PrintView;
import com.squareup.picasso.Picasso;
import com.unnamed.b.atv.model.TreeNode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserViewHolder extends TreeNode.BaseNodeViewHolder<UserViewHolder.UserItem> {
    @BindView(R.id.profile_pic) ImageView imageView;
    @BindView(R.id.user_name) TextView nameLabel;
    @BindView(R.id.size) TextView userNameLabel;
    public UserViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, UserItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.node_user, null, false);
        ButterKnife.bind(this, view);
        Picasso.get()
                .load(value.user.getProfilePic())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(imageView);
        nameLabel.setText(value.user.getName());
        userNameLabel.setText("@" + value.user.getUsername());

        return view;
    }

    @Override
    public void toggle(boolean active) {
    }


    public static class UserItem {
        public User user;

        public UserItem(User user) {
            this.user = user;
        }
        // rest will be hardcoded
    }

}
