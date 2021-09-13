package com.example.uhealth.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FPAdapter_Share extends FragmentPagerAdapter {
    private List<Fragment> fraglist = new ArrayList<>();
    private ArrayList<String> titlelist = new ArrayList<>();


    public FPAdapter_Share(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fraglist.get(position);
    }

    @Override
    public int getCount() {
        return fraglist.size();
    }

    public void addFragments(Fragment fragment, String title){
        fraglist.add(fragment);
        titlelist.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titlelist.get(position);
    }
}
