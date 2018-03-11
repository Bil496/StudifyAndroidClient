package com.bil496.studifyapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bil496.studifyapp.R;
import com.bil496.studifyapp.adapter.SubtopicAdapter;
import com.bil496.studifyapp.model.Subtopic;
import com.bil496.studifyapp.model.Topic;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by burak on 3/11/2018.
 */

public class EnrollDialog extends DialogFragment {
    @BindView(R.id.recycler_subtopics) RecyclerView recyclerView;
    @BindView(R.id.enroll_button) Button doneBtn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroll_dialog,container,false);
        ButterKnife.bind(this, view);
        Topic topic = (Topic) getArguments().getSerializable("topic");
        getDialog().setTitle("Sample");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new SubtopicAdapter(topic.getSubtopics(), R.layout.list_item_subtopic, getActivity()));
        doneBtn.setOnClickListener(doneAction);
        return view;
    }

    View.OnClickListener doneAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<Float> scores = getScores();
            Toast.makeText(getActivity(),scores.toString(),Toast.LENGTH_LONG).show();
        }
    };

    private List<Float> getScores() {
        List<Float> scores = new ArrayList<>();
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (recyclerView.findViewHolderForLayoutPosition(i) instanceof SubtopicAdapter.SubtopicViewHolder) {
                SubtopicAdapter.SubtopicViewHolder childHolder = (SubtopicAdapter.SubtopicViewHolder) recyclerView.findViewHolderForLayoutPosition(i);
                scores.add(childHolder.ratingBar.getRating());
            }
        }
        return scores;
    }
}
