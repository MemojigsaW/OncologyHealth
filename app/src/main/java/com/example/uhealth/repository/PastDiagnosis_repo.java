package com.example.uhealth.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.uhealth.Activity.PastDiagnosis;
import com.example.uhealth.DataModel.Diag_obj;
import com.example.uhealth.DataModel.timeline_item;
import com.example.uhealth.Interfaces.DataloadedListener;
import com.example.uhealth.utils.FireBaseInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PastDiagnosis_repo {
    private FireBaseInfo mFireBaseInfo;
    private final String PASTDIAGNOSIS = "PastDiagnosis";
    private final String ACCEPTEDREQUEST = "Accepted_Requests";

    private boolean fromInfoHistory;
    private String uid;
    private DataloadedListener listener;

    private Diag_obj diagObj;
    private String[] loadlist = {
            "Neurological",
            "Sensory",
            "Endocrine",
            "Cardiovascular",
            "Diseases",
            "Gastrointestinal",
            "Genitourinary",
            "Vascular",
            "Rheumatoid",
            "Cancers",
            "Miscellaneous"
    };

    public PastDiagnosis_repo(boolean FromInfoHistroy, String i_uid, DataloadedListener i_listener){
        mFireBaseInfo = new FireBaseInfo();
        fromInfoHistory = FromInfoHistroy;
        uid = i_uid;
        listener = i_listener;
        diagObj = new Diag_obj();
    }

    public MutableLiveData<Diag_obj> getObj(){
        loaduser();
        MutableLiveData<Diag_obj> data = new MutableLiveData<>();
        data.setValue(diagObj);
        return data;
    }

    private void loaduser() {
        mFireBaseInfo.mFirestore.collection(PASTDIAGNOSIS).document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData()==null){
                            listener.onDataLoaded();
                            return;
                        }else{
//                            submit made sure it is not null
                            Map<String, Object> loadmp =  (HashMap)documentSnapshot.getData();
                            diagObj.setAll_Diseases((List<String>)loadmp.get("Diseases"));
                            for (String key: loadlist){
                                diagObj.setOthers(key, loadmp.get(key).toString());
                            }
                        }
                        listener.onDataLoaded();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(PastDiagnosis.TAG, "onFailure: PD repo load fail");
                    }
                });
    }
}
