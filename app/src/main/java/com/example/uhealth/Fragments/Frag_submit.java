package com.example.uhealth.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.uhealth.Activity.Do_questions;
import com.example.uhealth.R;


public class Frag_submit extends Fragment {
    private Button mSubmit;

    public Frag_submit() {
        // Required empty public constructor
    }

    public static Frag_submit newInstance() {
        Frag_submit fragment = new Frag_submit();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_submit, container, false);
        mSubmit = view.findViewById(R.id.fragment_submit_submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Do_questions)getActivity()).submitAnswers();
            }
        });
        return view;
    }
}