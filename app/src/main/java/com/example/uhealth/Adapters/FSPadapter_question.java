package com.example.uhealth.Adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.uhealth.Activity.Do_questions;
import com.example.uhealth.Fragments.Frag_question;
import com.example.uhealth.Fragments.Frag_submit;

import java.util.ArrayList;

public class FSPadapter_question extends FragmentStatePagerAdapter {
    private ArrayList<String> list;
    private String qs_id;
    private Context mContext;

    public FSPadapter_question(@NonNull FragmentManager fm, int behavior,
                               ArrayList<String> input,
                               String qs_id) {
        super(fm, behavior);
        this.list = input;
        this.qs_id = qs_id;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position < list.size()){
            Frag_question frag_q = new Frag_question();
            Bundle bundle = new Bundle();
            String qid = list.get(position);
            bundle.putString("qid", qid);
            bundle.putString("qs_id", qs_id);
            bundle.putInt("position", position);
            frag_q.setArguments(bundle);
            return frag_q;
        }else{
            Frag_submit frag_s = new Frag_submit();
            return frag_s;
        }
    }

    @Override
    public int getCount() {
        return list.size()+1;
    }
}
