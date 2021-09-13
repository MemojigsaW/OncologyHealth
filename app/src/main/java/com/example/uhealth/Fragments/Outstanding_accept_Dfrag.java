package com.example.uhealth.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.uhealth.Activity.ShareFeature;
import com.example.uhealth.DataModel.Share_outstandings_item;
import com.example.uhealth.R;
import com.example.uhealth.ViewModel.Share_ViewModel;

import java.util.HashMap;

public class Outstanding_accept_Dfrag extends DialogFragment {
    private Share_ViewModel viewModel;
    private Context mContext;
    private Share_outstandings_item item;

    public Outstanding_accept_Dfrag(){
//        constructor
    }

    public static Outstanding_accept_Dfrag newInstance(){
        Outstanding_accept_Dfrag Dfrag = new Outstanding_accept_Dfrag();
        return Dfrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outstanding_accept, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView mTitle;
        CheckBox mInfoandHistory, mDiagnosis, mTimeline;
        Button mShare;

        mTitle = view.findViewById(R.id.DF_outstanding_accept_title);
        mInfoandHistory = view.findViewById(R.id.DF_outstanding_accept_personalandmedical);
        mDiagnosis = view.findViewById(R.id.DF_outstanding_accept_diagnosis);
        mTimeline = view.findViewById(R.id.DF_outstanding_accept_timeline);
        mShare = view.findViewById(R.id.DF_outstanding_accept_share);

        String title = "From "+item.getFrom_username();
        mTitle.setText(title);

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mInfoandHistory.isChecked() && !mDiagnosis.isChecked() && !mTimeline.isChecked()){
                    Toast.makeText(mContext, "Select least one", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, Boolean> b_map = new HashMap<>();
                b_map.put("InfoandHistory", mInfoandHistory.isChecked());
                b_map.put("Diagnosis", mDiagnosis.isChecked());
                b_map.put("Timeline", mTimeline.isChecked());
                ((ShareFeature)getActivity()).acceptOutstandings(b_map);
                dismiss();
            }
        });


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        viewModel = new ViewModelProvider(getActivity()).get(Share_ViewModel.class);
        item = viewModel.getOutstandingAcceptDialogItem();
    }
}
