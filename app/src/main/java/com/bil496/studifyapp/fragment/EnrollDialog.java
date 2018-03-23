package com.bil496.studifyapp.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.infideap.stylishwidget.view.AButton;
import com.bil496.studifyapp.R;
import com.bil496.studifyapp.TopicActivity;
import com.bil496.studifyapp.model.Subtopic;
import com.bil496.studifyapp.model.Talent;
import com.bil496.studifyapp.model.Topic;
import com.bil496.studifyapp.rest.APIError;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.rest.ErrorUtils;
import com.bil496.studifyapp.util.SharedPref;
import com.bil496.studifyapp.util.Status;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by burak on 3/11/2018.
 */

public class EnrollDialog extends DialogFragment {
    private static final String TAG = DialogFragment.class.getSimpleName();
    @BindView(R.id.root_layout) LinearLayout rootLayout;
    @BindView(R.id.enroll_button)
    AButton doneBtn;
    List<RatingBar> ratingBars;
    private Topic topic;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroll_dialog,container,false);
        ButterKnife.bind(this, view);
        topic = (Topic) getArguments().getSerializable("topic");
        getDialog().setTitle("Sample");
        doneBtn.setText("Enroll");
        doneBtn.setOnClickListener(doneAction);
        createSubtopic(topic.getSubtopics());
        return view;
    }

    View.OnClickListener doneAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doneBtn.setClickable(false);
            Talent[] talents = new Talent[topic.getSubtopics().size()];
            Integer userId = SharedPref.read(SharedPref.USER_ID, 0);
            for (int i = 0; i < talents.length; i++){
                Talent talent = new Talent(topic.getSubtopics().get(i).getId(), userId, (int)ratingBars.get(i).getRating());
                talents[i] = talent;
            }
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<ResponseBody> call = apiService.postTalents(userId, topic.getId(), talents);
            Log.e(TAG, call.toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody>call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        Log.d(TAG, response.toString());
                        SharedPref.write(SharedPref.CURRENT_TOPIC_ID, topic.getId());
                        Intent intent = new Intent(getActivity(), TopicActivity.class);
                        intent.putExtra("topicId", topic.getId());
                        intent.putExtra("topicName", topic.getTitle());
                        dismiss();
                        startActivity(intent);
                    }else{
                        APIError error = ErrorUtils.parseError(response);
                        if (error.status() == APIError.QUIT_TEAM_BEFORE_ENROLLING_TOPIC){
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("You are currently in a team?")
                                    .setContentText("You should quit your team!")
                                    .setCancelText("No, I <3 them")
                                    .setConfirmText("Yes, quit me!")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            ApiInterface apiService =
                                                    ApiClient.getClient().create(ApiInterface.class);
                                            Call<ResponseBody> quitCall = apiService.kickUser(SharedPref.read(SharedPref.USER_ID, -1), SharedPref.read(SharedPref.CURRENT_TEAM_ID, -1), SharedPref.read(SharedPref.USER_ID, -1));
                                            try {
                                                Response<ResponseBody> response = quitCall.execute();
                                                if(response.isSuccessful()){
                                                    Status.whenQuitTeam(getActivity());
                                                }else{
                                                    APIError error = ErrorUtils.parseError(response);
                                                    throw new Exception(error.message());
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();
                        }else{
                            // … and use it to show error information
                            Toast.makeText(getActivity(), error.message(), Toast.LENGTH_LONG).show();
                        }
                        // … or just log the issue like we’re doing :)
                        Log.d("error message", error.message());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody>call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                    doneBtn.setClickable(true);
                }
            });
        }
    };

    private void createSubtopic(List<Subtopic> subtopics) {
        ratingBars = new ArrayList<>();
        for (Subtopic subtopic : subtopics){
            LinearLayout parent = new LinearLayout(getActivity());
            parent.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            parent.setPadding(16, 16, 16, 16);
            parent.setOrientation(LinearLayout.VERTICAL);
            MaterialRatingBar ratingBar = new MaterialRatingBar(getActivity());
            ratingBar.setStepSize(1f);
            ratingBar.setNumStars(5);
            TextView textView = new TextView(getActivity());
            textView.setText(subtopic.getTitle());
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            parent.setGravity(Gravity.CENTER_HORIZONTAL);
            parent.addView(textView);
            parent.addView(ratingBar);
            rootLayout.addView(parent);
            ratingBars.add(ratingBar);
        }
    }
}
