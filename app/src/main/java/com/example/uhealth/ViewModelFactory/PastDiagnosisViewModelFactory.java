package com.example.uhealth.ViewModelFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.uhealth.Interfaces.DataloadedListener;
import com.example.uhealth.ViewModel.PastDiagnosis_ViewModel;

public class PastDiagnosisViewModelFactory implements ViewModelProvider.Factory {
    private boolean fromInfoHistory;
    private String idKey;
    private DataloadedListener listener;

    public PastDiagnosisViewModelFactory(boolean i_fromInfoHistory, String _idKey, DataloadedListener i_listener){
        fromInfoHistory = i_fromInfoHistory;
        idKey = _idKey;
        listener = i_listener;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PastDiagnosis_ViewModel.class)){
            return (T) new PastDiagnosis_ViewModel(fromInfoHistory, idKey, listener);
        }else{
            throw new IllegalArgumentException("PD_VM factoru exception");
        }
    }
}
