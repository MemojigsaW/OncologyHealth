package com.example.uhealth.ViewModelFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.uhealth.Interfaces.DataloadedListener;
import com.example.uhealth.Interfaces.ShareDataLoadedListener;
import com.example.uhealth.ViewModel.Share_ViewModel;

public class ShareViewModelFactory implements ViewModelProvider.Factory {
    private ShareDataLoadedListener listener;

    public ShareViewModelFactory(ShareDataLoadedListener implemented){
        listener = implemented;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(Share_ViewModel.class)){
            return (T) new Share_ViewModel(listener);
        }else{
            throw new IllegalArgumentException("Share VM Factory arg Exception");
        }
    }
}
