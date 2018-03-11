package com.bil496.studifyapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bil496.studifyapp.R;
import com.bil496.studifyapp.model.Topic;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by burak on 3/11/2018.
 */

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>{
    private List<Topic> topics;
    private int rowLayout;
    private Context context;

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.topics_layout) LinearLayout linearLayout;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.size) TextView size;


        public TopicViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
    public TopicAdapter(List<Topic> topics, int rowLayout, Context context) {
        this.topics = topics;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public TopicAdapter.TopicViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new TopicViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TopicViewHolder holder, final int position) {
        holder.title.setText(topics.get(position).getTitle());
        holder.size.setText(topics.get(position).getSize().toString());
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }
}
