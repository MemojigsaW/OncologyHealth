package com.example.uhealth.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.FileUriExposedException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.uhealth.R;

public class Frag_questionnaire_details extends Fragment {
    private String description;
    private String interpretation;
    private String furthermore;
    private String title;
    TextView mDes, mInt, mFur, mAbout;

    public Frag_questionnaire_details(String i_title, String Description, String Interpretation, String FurtherMore) {
        // Required empty public constructor
        title = i_title;
        description = Description;
        interpretation = Interpretation;
        furthermore = FurtherMore;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag__questionnaire_detail, container, false);
        mDes = view.findViewById(R.id.Description);
        mInt = view.findViewById(R.id.Interpretation);
        mFur = view.findViewById(R.id.Further_Info);
        mAbout = view.findViewById(R.id.abouts);

        mDes.setText(description);
        mInt.setText(interpretation);
        mFur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(furthermore);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
        mAbout.setText("About "+title);
        return view;
    }
}