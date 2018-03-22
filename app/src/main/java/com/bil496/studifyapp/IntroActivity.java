package com.bil496.studifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bil496.studifyapp.model.Place;
import com.bil496.studifyapp.rest.ApiClient;
import com.bil496.studifyapp.rest.ApiInterface;
import com.bil496.studifyapp.util.SharedPref;
import com.shashank.sony.fancywalkthroughlib.FancyWalkthroughActivity;
import com.shashank.sony.fancywalkthroughlib.FancyWalkthroughCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by burak on 3/13/2018.
 */

public class IntroActivity extends FancyWalkthroughActivity {
    private List<Place> placeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPref.init(getApplicationContext());
        if (!SharedPref.read(SharedPref.IS_FIRST, true)){
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        placeList = new ArrayList<>();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<Place[]> call = apiService.getPlaces();
        call.enqueue(new Callback<Place[]>() {
                         @Override
                         public void onResponse(Call<Place[]> call, Response<Place[]> response) {
                            placeList = new ArrayList<>(Arrays.asList(response.body()));
                         }

                         @Override
                         public void onFailure(Call<Place[]> call, Throwable t) {

                         }
                     });
        FancyWalkthroughCard fancywalkthroughCard1 = new FancyWalkthroughCard("Find Topic", "Find or create the topic you need to study in your college.", R.drawable.find_topic);
        FancyWalkthroughCard fancywalkthroughCard2 = new FancyWalkthroughCard("Criticize Your Skills", "Give your skills a score, so that we can recommend you teams you should involve.",R.drawable.criticize_skills);
        FancyWalkthroughCard fancywalkthroughCard3 = new FancyWalkthroughCard("Apply a Team", "Based on our recommendation select a team to join.",R.drawable.apply);
        FancyWalkthroughCard fancywalkthroughCard4 = new FancyWalkthroughCard("Start to Studify", "If they accept your request, get ready to study. \nIf they deny, fuck it, you can create your own team!",R.drawable.accepted);

        fancywalkthroughCard1.setBackgroundColor(R.color.white);
        fancywalkthroughCard1.setIconLayoutParams(300,300,0,0,0,0);
        fancywalkthroughCard2.setBackgroundColor(R.color.white);
        fancywalkthroughCard2.setIconLayoutParams(300,300,0,0,0,0);
        fancywalkthroughCard3.setBackgroundColor(R.color.white);
        fancywalkthroughCard3.setIconLayoutParams(300,300,0,0,0,0);
        fancywalkthroughCard4.setBackgroundColor(R.color.white);
        fancywalkthroughCard4.setIconLayoutParams(300,300,0,0,0,0);
        List<FancyWalkthroughCard> pages = new ArrayList<>();

        pages.add(fancywalkthroughCard1);
        pages.add(fancywalkthroughCard2);
        pages.add(fancywalkthroughCard3);
        pages.add(fancywalkthroughCard4);

        for (FancyWalkthroughCard page : pages) {
            page.setTitleColor(R.color.black);
            page.setDescriptionColor(R.color.black);
        }
        setFinishButtonTitle("Get Started");
        showNavigationControls(true);
        setColorBackground(R.color.colorGreen);
        setInactiveIndicatorColor(R.color.grey_600);
        setActiveIndicatorColor(R.color.colorGreen);
        setOnboardPages(pages);

    }

    @Override
    public void onFinishButtonPressed() {
        String[] colleges = new String[placeList.size()];
        for (int i = 0; i < colleges.length; i++){
            colleges[i] = placeList.get(i).getTitle();
        }
        new MaterialDialog.Builder(this)
                .title("But first, tell us your College")
                .items(colleges)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        SharedPref.write(SharedPref.IS_FIRST, false);
                        SharedPref.write(SharedPref.LOCATION_ID, placeList.get(which).getId());
                        SharedPref.write(SharedPref.LOCATION_TITLE, placeList.get(which).getTitle());

                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                })
                .positiveText("That's my college")
                .show();
    }
}