package com.bil496.studifyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.infideap.stylishwidget.view.AButton;
import com.app.infideap.stylishwidget.view.AEditText;
import com.app.infideap.stylishwidget.view.ATextInputEditText;
import com.bil496.studifyapp.model.Subtopic;
import com.bil496.studifyapp.model.Topic;
import com.nex3z.flowlayout.FlowLayout;
import com.robertlevonyan.views.chip.Chip;
import com.robertlevonyan.views.chip.OnCloseClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by burak on 3/11/2018.
 */

public class TopicFormActivity extends AppCompatActivity {
    @BindView(R.id.topic_title)
    ATextInputEditText topicTitle;
    @BindView(R.id.editText)
    AEditText mEditText;
    @BindView(R.id.button)
    AButton mButton;
    @BindView(R.id.chip_container)
    FlowLayout chipContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_form);
        ButterKnife.bind(this);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Chip chip = new Chip(TopicFormActivity.this);
                chip.setChipText(mEditText.getText().toString().trim());
                chip.setClosable(true);
                chipContainer.addView(chip);
                chip.setOnCloseClickListener(new OnCloseClickListener() {
                    @Override
                    public void onCloseClick(View v) {
                        chipContainer.removeView(chip);
                    }
                });
                mEditText.setText("");
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
        inflater.inflate(R.menu.form_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_send){
            if(topicTitle.getText().toString().trim().length() > 0){
                Topic topic = new Topic();
                topic.setTitle(topicTitle.getText().toString().trim());
                List<Subtopic> subtopics = new ArrayList<>();
                for(int i = 0; i < chipContainer.getChildCount(); i++) {
                    Chip chip = (Chip) chipContainer.getChildAt(i);
                    Subtopic subtopic = new Subtopic();
                    subtopic.setTitle(chip.getChipText());
                    subtopics.add(subtopic);
                }
                topic.setSize(0);
                topic.setSubtopics(subtopics);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("topic",topic);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }else {
                Toast.makeText(getBaseContext(), "Enter a valid title", Toast.LENGTH_LONG).show();
            }
        }else if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

}
