package com.bil496.studifyapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bil496.studifyapp.fragment.PlaceSlide;
import com.bil496.studifyapp.model.Place;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.util.SharedPref;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by burak on 3/11/2018.
 */

public class IntroActivity extends MaterialIntroActivity {
    private static final String TAG = IntroActivity.class.getSimpleName();
    private PlaceSlide placeSlide;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPref.init(getApplicationContext());
        if (!SharedPref.read(SharedPref.IS_FIRST, true)){
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        placeSlide = new PlaceSlide();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<Place[]> call = apiService.getPlaces();
        call.enqueue(new Callback<Place[]>() {
            @Override
            public void onResponse(Call<Place[]>call, Response<Place[]> response) {

                List<Place> places = new ArrayList<Place>(Arrays.asList(response.body()));
                Log.d(TAG, "Number of place received: " + places.size());
                Bundle bundle = new Bundle();
                bundle.putSerializable("places", (Serializable) places);
                // set Fragmentclass Arguments
                placeSlide.setArguments(bundle);


                enableLastSlideAlphaExitTransition(true);

                getBackButtonTranslationWrapper()
                        .setEnterTranslation(new IViewTranslation() {
                            @Override
                            public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                                view.setAlpha(percentage);
                            }
                        });

                addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.first_slide_background)
                        .buttonsColor(R.color.first_slide_buttons)
                        .title("Welcome to Studify")
                        .description("Let me ask you something!")
                        .build());

                addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.second_slide_background)
                        .buttonsColor(R.color.second_slide_buttons)
                        .title("Which place you will be?")
                        .build());

                addSlide(placeSlide);

                addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.fourth_slide_background)
                        .buttonsColor(R.color.fourth_slide_buttons)
                        .title("That's it")
                        .description("You should login now!")
                        .build());
            }

            @Override
            public void onFailure(Call<Place[]>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onFinish() {
        super.onFinish();
        SharedPref.write(SharedPref.IS_FIRST, false);
        SharedPref.write(SharedPref.LOCATION_ID, placeSlide.getPlace().getId());
        SharedPref.write(SharedPref.LOCATION_TITLE, placeSlide.getPlace().getTitle());

        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Selected place is " + placeSlide.getPlace().getTitle() +"! :)", Toast.LENGTH_SHORT).show();
    }
}
