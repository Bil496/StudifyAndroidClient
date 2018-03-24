package com.bil496.studifyapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.infideap.stylishwidget.util.TextViewUtils;
import com.bil496.studifyapp.R;
import com.bil496.studifyapp.model.Topic;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by burak on 3/11/2018.
 */

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> implements Filterable{
    private List<Topic> topics;
    private List<Topic> filteredTopics;
    private int rowLayout;
    private Context context;
    CustomItemClickListener listener;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredTopics = topics;
                } else {
                    List<Topic> filteredList = new ArrayList<>();
                    for (Topic row : topics) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filteredTopics = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredTopics;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredTopics = (ArrayList<Topic>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.size) TextView size;


        public TopicViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
    public TopicAdapter(List<Topic> topics, int rowLayout, Context context, CustomItemClickListener listener) {
        this.topics = topics;
        this.rowLayout = rowLayout;
        this.context = context;
        this.listener = listener;
        this.filteredTopics = topics;
    }

    @Override
    public TopicAdapter.TopicViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final TopicViewHolder viewHolder = new TopicViewHolder(view);
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(TopicViewHolder holder, final int position) {
        holder.title.setText(filteredTopics.get(position).getTitle());
        holder.size.setText(filteredTopics.get(position).getSize().toString());
        TextViewUtils utils = TextViewUtils.getInstance();
        //Increment
        Thread incrementThread = utils.printIncrement(holder.size, "%d active users", filteredTopics.get(position).getSize(), 500);
    }

    @Override
    public int getItemCount() {
        return filteredTopics.size();
    }

    public Topic getFilteredItem(int position){
        return filteredTopics.get(position);
    }
}
