package com.example.uhealth.Fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.uhealth.Activity.Timeline;
import com.example.uhealth.Adapters.rvadapter_tlfiltertype;
import com.example.uhealth.ViewModel.TimelineViewModel;
import com.example.uhealth.R;

import java.util.Calendar;

public class TimelineFilter extends DialogFragment {
    private Timeline.dialogListener listener;

    public TimelineFilter(Timeline.dialogListener in_listener) {
        // Required empty public constructor
        listener = in_listener;
    }

    public static TimelineFilter newInstance(Timeline.dialogListener in_listener) {
        TimelineFilter fragment = new TimelineFilter(in_listener);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline_filter, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TimelineViewModel timelineViewModel = new ViewModelProvider(getActivity()).get(TimelineViewModel.class);

        TextView tv = view.findViewById(R.id.tlfilter_title);
        ToggleButton tbdate = view.findViewById(R.id.tlfilter_datetoggle);
        NumberPicker np = view.findViewById(R.id.tlfilter_date);
        RecyclerView rv = view.findViewById(R.id.tlfilter_filteroption);

        //        Number picker
        np.setMinValue(1950);
        Calendar now = Calendar.getInstance();
        np.setMaxValue(now.get(Calendar.YEAR));
        if (timelineViewModel.get_date() >=0){
            np.setVisibility(View.VISIBLE);
            np.setValue(timelineViewModel.get_date());
        }
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                timelineViewModel.set_date(newVal);
            }
        });

//        Toggle
        if (timelineViewModel.get_date()>=0){
            tbdate.setChecked(true);
        }
        tbdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    np.setVisibility(View.VISIBLE);
                    int date = timelineViewModel.get_date();
                    if (date<0){
                        np.setValue(now.get(Calendar.YEAR));
                    }
                }else{
                    np.setVisibility(View.INVISIBLE);
                    timelineViewModel.set_date(-1);
                }
            }
        });

//        Recyclerview for items
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(layoutManager);

        String[] filteroptions = getResources().getStringArray(R.array.Appointment_types);
        RecyclerView.Adapter mAdapter = new rvadapter_tlfiltertype(filteroptions, new filtercallback() {
            @Override
            public void rmoption(String input) {
                timelineViewModel.rm_from_arraylist(input);
            }

            @Override
            public void addoption(String input) {
                timelineViewModel.add_to_arraylist(input);
            }

            @Override
            public boolean checkoption(String input) {
                return timelineViewModel.checkifin(input);
            }
        });
        rv.setAdapter(mAdapter);
        rv.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.filterlistener();
        listener.jumppagelistener();
    }

    public interface filtercallback{
        void rmoption(String input);
        void addoption(String input);
        boolean checkoption(String input);
    }
}