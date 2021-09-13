package com.example.uhealth.Adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.uhealth.Fragments.Frag_healthtip;

public class FSPadapter_healthtip extends FragmentStatePagerAdapter {
    private String[] tiplist;

    public FSPadapter_healthtip(@NonNull FragmentManager fm, int behavior, String[] i_list) {
        super(fm, behavior);
        tiplist = i_list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position < tiplist.length){
            String tip = tiplist[position];
            Frag_healthtip frag = new Frag_healthtip();
            Bundle bundle = new Bundle();
            bundle.putString("TIP", tip);
            frag.setArguments(bundle);
            return frag;
        }else{
//            place holder fragment to reset to beginning
            Frag_healthtip frag = new Frag_healthtip();
            Bundle bundle = new Bundle();
            bundle.putString("TIP", "");
            frag.setArguments(bundle);
            return frag;
        }
    }

    @Override
    public int getCount() {
        if (tiplist!=null){
            return tiplist.length+1;
        }else{
            return 0;
        }
    }
}
