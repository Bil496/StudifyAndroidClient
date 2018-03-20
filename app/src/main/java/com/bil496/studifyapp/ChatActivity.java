package com.bil496.studifyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bil496.studifyapp.adapter.ChatMessageAdapter;
import com.bil496.studifyapp.adapter.RealmMessagesAdapter;
import com.bil496.studifyapp.model.ChatMessage;
import com.bil496.studifyapp.realm.RealmController;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by burak on 3/20/2018.
 */

public class ChatActivity extends AbstractObservableActivity {
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.btn_send) Button mButtonSend;
    @BindView(R.id.et_message) EditText mEditTextMessage;

    private ChatMessageAdapter adapter;
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        //get realm instance
        this.realm = RealmController.with(this).getRealm();
        setupRecycler();
        // refresh the realm instance
        RealmController.with(this).refresh();
        setRealmAdapter(RealmController.with(this).getMessages());

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(message);
                mEditTextMessage.setText("");
            }
        });
    }

    public void setRealmAdapter(RealmResults<ChatMessage> messages) {
        RealmMessagesAdapter realmAdapter = new RealmMessagesAdapter(this.getApplicationContext(), messages, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager since the messages are vertically scrollable
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create an empty adapter and add it to the recycler view
        adapter = new ChatMessageAdapter(this);
        mRecyclerView.setAdapter(adapter);
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message);
        chatMessage.setId(RealmController.getInstance().getMessages().size());
        RealmController.with(this).addMessage(chatMessage);
        adapter.notifyDataSetChanged();
        // scroll the recycler view to bottom
        mRecyclerView.scrollToPosition(chatMessage.getId());
    }

    private void recieveMessage(String message) {
        //ChatMessage chatMessage = new ChatMessage(message, true, false);
        //mAdapter.add(chatMessage);
    }
}
