package com.bil496.studifyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.infideap.stylishwidget.view.AButton;
import com.bil496.studifyapp.adapter.ChatMessageAdapter;
import com.bil496.studifyapp.adapter.RealmMessagesAdapter;
import com.bil496.studifyapp.model.ChatMessage;
import com.bil496.studifyapp.model.Payload;
import com.bil496.studifyapp.realm.RealmController;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.util.SharedPref;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by burak on 3/20/2018.
 */

public class ChatActivity extends AbstractObservableActivity {
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.btn_send)
    AButton mButtonSend;
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

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_clear_chat:
                RealmController.with(this).clearAll();
                adapter.notifyDataSetChanged();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
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

    private void sendMessage(final String message) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiService.sendMessage(SharedPref.read(SharedPref.USER_ID, -1), message);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    ChatMessage chatMessage = new ChatMessage(message);
                    chatMessage.setId(RealmController.getInstance().getMessagesCount());
                    RealmController.with(ChatActivity.this).addMessage(chatMessage);
                    adapter.notifyDataSetChanged();
                    // scroll the recycler view to bottom
                    mRecyclerView.scrollToPosition(chatMessage.getId());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void recieveMessage() {
        adapter.notifyDataSetChanged();
        // scroll the recycler view to bottom
        mRecyclerView.scrollToPosition(RealmController.getInstance().getMessagesCount() - 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerView.scrollToPosition(RealmController.getInstance().getMessagesCount() - 1);
    }

    @Override
    protected void onNotification(Payload payload) {
        super.onNotification(payload);
        if(payload.getType() == Payload.Type.CHAT_MESSAGE){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recieveMessage();
                }
            });
        }
    }
}
