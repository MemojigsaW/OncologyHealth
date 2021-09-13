package com.example.uhealth.ViewModelFactory;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.uhealth.Interfaces.DataloadedListener;
import com.example.uhealth.ViewModel.TimelineViewModel;

public class TimelineViewModelFactory implements ViewModelProvider.Factory {
    private String[] arraylist;
    private DataloadedListener listener;
    private String uid;

    public TimelineViewModelFactory(String[] inputlist, String uidKey, DataloadedListener implemented){
        arraylist = inputlist;
        listener = implemented;
        uid = uidKey;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TimelineViewModel.class)){
            return (T) new TimelineViewModel(arraylist, uid, listener);
        }else{
            throw new IllegalArgumentException("VM factory exception");
        }
    }
}
