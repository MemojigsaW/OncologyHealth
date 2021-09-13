package com.example.uhealth.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uhealth.DataModel.Diag_obj;
import com.example.uhealth.Interfaces.DataloadedListener;
import com.example.uhealth.repository.PastDiagnosis_repo;

import java.util.ArrayList;
import java.util.List;

public class PastDiagnosis_ViewModel extends ViewModel {
    private boolean fromInfoHistory;
    private String uid;
    private DataloadedListener listener;
    private MutableLiveData<Diag_obj> diag_objMutableLiveData;

    private PastDiagnosis_repo repo;

    private List<Integer> openPositions;

    public PastDiagnosis_ViewModel(boolean i_fromInfoHistory, String i_uid, DataloadedListener i_listener){
        fromInfoHistory = i_fromInfoHistory;
        uid = i_uid;
        listener = i_listener;
        listener.onBeforeLoaded();
        repo = new PastDiagnosis_repo(fromInfoHistory, uid, listener);
        diag_objMutableLiveData = repo.getObj();
        openPositions = new ArrayList<>();
    }


    public LiveData<Diag_obj> getDiag_objMutableLiveData() {
        return diag_objMutableLiveData;
    }

    public MutableLiveData<Diag_obj> getMutable(){
        return diag_objMutableLiveData;
    }

    public boolean containsPosition(int position){
        return openPositions.contains(position);
    }
    public void rmPosition(int pos){
        assert openPositions.contains(pos);
        openPositions.remove(Integer.valueOf(pos));
    }
    public void addPosition(int pos){
        assert !openPositions.contains(pos);
        openPositions.add(pos);
    }

}
