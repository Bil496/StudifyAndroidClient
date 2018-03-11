package com.bil496.studifyapp.fragment;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.bil496.studifyapp.R;
import com.bil496.studifyapp.model.Place;

import java.util.ArrayList;
import java.util.List;

import agency.tango.materialintroscreen.SlideFragment;

import static android.R.id.list;

/**
 * Created by burak on 3/11/2018.
 */

public class PlaceSlide extends SlideFragment {
    private Spinner spinner;
    private List<Place> placeList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_place_slide, container, false);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        List<String> list = new ArrayList<>();
        placeList = (List<Place>)getArguments().getSerializable("places");
        for (Place place : placeList){
            list.add(place.getTitle());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,list);
        spinner.setAdapter(adapter);
        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.custom_slide_background;
    }

    @Override
    public int buttonsColor() {
        return R.color.custom_slide_buttons;
    }

    @Override
    public boolean canMoveFurther() {
        return placeList != null;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return "";
    }

    public Place getPlace() {
        return placeList.get(spinner.getSelectedItemPosition());
    }
}
