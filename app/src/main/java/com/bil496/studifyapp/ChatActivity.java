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
import com.bil496.studifyapp.model.ChatMessage;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by burak on 3/20/2018.
 */

public class ChatActivity extends AbstractObservableActivity {
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.btn_send) Button mButtonSend;
    @BindView(R.id.et_message) EditText mEditTextMessage;


    private ChatMessageAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mRecyclerView.setAdapter(mAdapter);

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

    private void sendMessage(String message) {
        //ChatMessage chatMessage = new ChatMessage(message, true, false);
        //mAdapter.add(chatMessage);
    }

    private void recieveMessage(String message) {
        //ChatMessage chatMessage = new ChatMessage(message, true, false);
        //mAdapter.add(chatMessage);
    }
}
