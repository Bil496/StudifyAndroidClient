package com.bil496.studifyapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bil496.studifyapp.R;
import com.bil496.studifyapp.model.ChatMessage;
import com.bil496.studifyapp.realm.RealmController;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import me.himanshusoni.chatmessageview.ChatMessageView;

/**
 * Created by burak on 3/20/2018.
 */

public class ChatMessageAdapter extends RealmRecyclerViewAdapter<ChatMessage> {
    private final String TAG = "ChatMessageAdapter";
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, OTHER_MESSAGE_SEQUENCE = 2;

    private Context mContext;
    private Realm realm;
    private LayoutInflater inflater;

    public ChatMessageAdapter(Context context) {
        mContext = context;
        realm = RealmController.getInstance().getRealm();
    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);
        if (item.isMine()) return MY_MESSAGE;
        else{
            if(position == 0 || !getItem(position - 1).getSenderName().equals(item.getSenderName()))
                return OTHER_MESSAGE;
            else
                return OTHER_MESSAGE_SEQUENCE;
        }
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MY_MESSAGE) {
            return new MessageHolder(LayoutInflater.from(mContext).inflate(R.layout.item_mine_message, parent, false));
        } else {
            return new MessageHolder(LayoutInflater.from(mContext).inflate(R.layout.item_other_message, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        // cast the generic view holder to our specific one
        final MessageHolder holder = (MessageHolder) viewHolder;

        ChatMessage chatMessage = getItem(position);
        holder.tvMessage.setText(chatMessage.getContent());
        String date = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());
        holder.tvTime.setText(date);

        if(chatMessage.isMine() == false){
            if (viewHolder.getItemViewType() == OTHER_MESSAGE){
                holder.tvSenderName.setText(chatMessage.getSenderName());
                Picasso.get()
                        .load(chatMessage.getSenderImage())
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                        .into(holder.ivSenderImage);
            }else{
                holder.tvSenderName.setVisibility(View.GONE);
                holder.ivSenderImage.setVisibility(View.GONE);
            }
        }

        holder.chatMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public static class MessageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chatMessageView)
        ChatMessageView chatMessageView;
        @BindView(R.id.tv_message)
        TextView tvMessage;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.sender_name)
        TextView tvSenderName;
        @BindView(R.id.sender_image)
        ImageView ivSenderImage;

        MessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}