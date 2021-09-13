package com.example.uhealth.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.uhealth.Activity.ShareFeature;
import com.example.uhealth.DataModel.Share_accepted_item;
import com.example.uhealth.DataModel.Share_outstandings_item;
import com.example.uhealth.Interfaces.ShareDataLoadedListener;
import com.example.uhealth.utils.FireBaseInfo;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Share_repo {
    private final String OUTSTANDINGS = "Outstanding_Requests";
    private final String ACCEPTED = "Accepted_Requests";

    private static Share_repo Instance;

    private FireBaseInfo mFireBaseInfo;
    static ShareDataLoadedListener listener;
    private List<Share_outstandings_item> outstandingsList =new ArrayList<>();
    private List<Share_accepted_item> acceptedList = new ArrayList<>();

    public Share_repo(){
        mFireBaseInfo = new FireBaseInfo();
    }

    public static Share_repo getInstance(ShareDataLoadedListener in_listener){
//        singleton design is not needed fro firebase connections
//        if (Instance==null){
//            Instance = new Share_repo();
//        }
        Instance = new Share_repo();
        listener = in_listener;
        return Instance;
    }

    public MutableLiveData<List<Share_outstandings_item>> getOutstandings(){
        loadOutstandings();
        MutableLiveData<List<Share_outstandings_item>> data = new MutableLiveData<>();
        data.setValue(outstandingsList);
        return data;
    }

    public MutableLiveData<List<Share_accepted_item>> getAccepteds(){
        loadAccepteds();
        MutableLiveData<List<Share_accepted_item>> data = new MutableLiveData<>();
        data.setValue(acceptedList);
        return data;
    }

    private void loadAccepteds() {
        mFireBaseInfo.mFirestore.collection(ACCEPTED)
                .whereEqualTo("from_Id", mFireBaseInfo.mUser.getUid())
                .orderBy("expire", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            Log.d(ShareFeature.TAG, "load accepted onsnapshot failed");
                        }
                        else {
                            //                        init list
                            acceptedList.clear();
                            for (QueryDocumentSnapshot queryDocumentSnapshot: value){
                                HashMap<String,Object> resMap = (HashMap)queryDocumentSnapshot.getData();
                                Share_accepted_item item = new Share_accepted_item(resMap);
                                item.setDocumentId(queryDocumentSnapshot.getId());
                                item.setmInfoandHistory((boolean)queryDocumentSnapshot.getData().get("InfoandHistory"));
                                item.setmDiagnosis((boolean)queryDocumentSnapshot.getData().get("Diagnosis"));
                                item.setmTimeline((boolean)queryDocumentSnapshot.getData().get("Timeline"));
                                acceptedList.add(item);
                            }
                            listener.onAcceptedLoaded();
                        }
                    }
                });
    }

    private void loadOutstandings() {
        mFireBaseInfo.mFirestore.collection(OUTSTANDINGS)
                .whereEqualTo("to_Id", mFireBaseInfo.mUser.getUid())
                .orderBy("expire", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            Log.d(ShareFeature.TAG, "load outstandings onsnapshot failed");
                        }
                        else {
                            //                        init list
                            outstandingsList.clear();
                            for (QueryDocumentSnapshot queryDocumentSnapshot: value){
                                HashMap<String,Object> resMap = (HashMap)queryDocumentSnapshot.getData();
                                Share_outstandings_item item = new Share_outstandings_item(resMap);
                                item.setDocumentId(queryDocumentSnapshot.getId());
                                outstandingsList.add(item);
                            }
                            listener.onOutstandingsLoaded();
                        }
                    }
                });
    }
}
