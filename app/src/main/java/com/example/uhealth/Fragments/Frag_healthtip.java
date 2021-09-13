package com.example.uhealth.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.uhealth.R;

public class Frag_healthtip extends Fragment {
    private String tip;
    private TextView textView;

    public Frag_healthtip() {
        // Required empty public constructor
    }


    public static Frag_healthtip newInstance() {
        Frag_healthtip fragment = new Frag_healthtip();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tip = getArguments().getString("TIP");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_healthtip, container, false);
        textView = view.findViewById(R.id.inner_msg);
        textView.setText(tip);
        return view;
    }
}