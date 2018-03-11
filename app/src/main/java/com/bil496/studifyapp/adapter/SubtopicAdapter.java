package com.bil496.studifyapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bil496.studifyapp.R;
import com.bil496.studifyapp.model.Subtopic;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by burak on 3/11/2018.
 */

public class SubtopicAdapter extends RecyclerView.Adapter<SubtopicAdapter.SubtopicViewHolder>{
    private List<Subtopic> subtopics;
    private int rowLayout;
    private Context context;

    public static class SubtopicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.subtopics_layout) LinearLayout linearLayout;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.ratingBar) public RatingBar ratingBar;


        public SubtopicViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
    public SubtopicAdapter(List<Subtopic> subtopics, int rowLayout, Context context) {
        this.subtopics = subtopics;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public SubtopicAdapter.SubtopicViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final SubtopicViewHolder viewHolder = new SubtopicViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(SubtopicViewHolder holder, final int position) {
        holder.title.setText(subtopics.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return subtopics.size();
    }
}
