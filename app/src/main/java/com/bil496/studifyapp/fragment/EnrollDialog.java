package com.bil496.studifyapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bil496.studifyapp.IntroActivity;
import com.bil496.studifyapp.R;
import com.bil496.studifyapp.adapter.CustomItemClickListener;
import com.bil496.studifyapp.adapter.SubtopicAdapter;
import com.bil496.studifyapp.adapter.TopicAdapter;
import com.bil496.studifyapp.model.Talent;
import com.bil496.studifyapp.model.Topic;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.util.SharedPref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by burak on 3/11/2018.
 */

public class EnrollDialog extends DialogFragment {
    private static final String TAG = DialogFragment.class.getSimpleName();
    @BindView(R.id.recycler_subtopics) RecyclerView recyclerView;
    @BindView(R.id.enroll_button) Button doneBtn;
    private Topic topic;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroll_dialog,container,false);
        ButterKnife.bind(this, view);
        topic = (Topic) getArguments().getSerializable("topic");
        getDialog().setTitle("Sample");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new SubtopicAdapter(topic.getSubtopics(), R.layout.list_item_subtopic, getActivity()));
        doneBtn.setText("Enroll");
        doneBtn.setOnClickListener(doneAction);
        return view;
    }

    View.OnClickListener doneAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<Float> scores = getScores();
            Talent[] talents = new Talent[scores.size()];
            Integer userId = SharedPref.read(SharedPref.USER_ID, 0);
            for (int i = 0; i < talents.length; i++){
                Talent talent = new Talent(topic.getSubtopics().get(i).getId(), userId, scores.get(i));
                talents[i] = talent;
            }
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<String> call = apiService.postTalents(userId, topic.getId(), talents);
            Log.e(TAG, call.toString());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String>call, Response<String> response) {
                    Log.e(TAG, response.body());
                }

                @Override
                public void onFailure(Call<String>call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });
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
