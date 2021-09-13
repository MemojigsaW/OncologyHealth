package com.example.uhealth.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.uhealth.Activity.Timeline;
import com.example.uhealth.Appointment;
import com.example.uhealth.Appointment  ;
import com.example.uhealth.DataModel.timeline_item;
import com.example.uhealth.Interfaces.DataloadedListener;
import com.example.uhealth.Medication;
import com.example.uhealth.Medication  ;
import com.example.uhealth.utils.FireBaseInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class timeline_repo {

//    private static final String APPOINTMENTS ="appointment_testing_alan";//"AAppointment";//
//    private static final String MEDICATIONS = "medication_testing_alan";//"MMedication";//

    private static final String APPOINTMENTS ="AAppointment";//"appointment_testing_alan";//
    private static final String MEDICATIONS = "MMedication";//"medication_testing_alan";//

    private FireBaseInfo mFirebaseInfo;
    private List<Medication  > med_list = new ArrayList<>();
    private List<timeline_item> tl_list = new ArrayList<>();

    private DataloadedListener dataloadedListener;

    private String uid;

    public timeline_repo (DataloadedListener in_listener, String i_uid){

        mFirebaseInfo = new FireBaseInfo();
        dataloadedListener = in_listener;
        uid = i_uid;
    }

    public MutableLiveData<List<timeline_item>> getItems(){
        loaddata();
        MutableLiveData<List<timeline_item>> data = new MutableLiveData<>();
        data.setValue(tl_list);
        return data;
    }

    private void loaddata(){
        Query query_apps, query_meds;
        query_apps = mFirebaseInfo.mFirestore
                .collection(APPOINTMENTS)
                .whereEqualTo("patientid", uid)
                .orderBy("date", Query.Direction.DESCENDING);
        query_meds = mFirebaseInfo.mFirestore
                .collection(MEDICATIONS)
                .whereEqualTo("uid", uid)
                .orderBy("initdate", Query.Direction.DESCENDING);

        Task t_app = query_apps.get();
        Task t_med = query_meds.get();

        MutableLiveData<List<timeline_item>> result = new MutableLiveData<>();

        Task combinedtask = Tasks.whenAllComplete(t_app, t_med)
                .addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                    @Override
                    public void onSuccess(List<Task<?>> tasks) {
                        for (Task task:tasks){
                            if (!task.isSuccessful()){
                                Log.d(Timeline.TAG, "Least one task failed in repo");
                                return;
                            }
                        }
                        Log.d(Timeline.TAG, "All tasks successful in repo");
                        QuerySnapshot q_app = (QuerySnapshot) tasks.get(0).getResult();
                        QuerySnapshot q_med = (QuerySnapshot) tasks.get(1).getResult();

//                            global index to add meds to apps
                        int g_index = 0;
                        med_list.clear();
                        tl_list.clear();

                        for (QueryDocumentSnapshot documentSnapshot: q_med){
                            HashMap<String,Object> resMap = (HashMap)documentSnapshot.getData();
                                Medication   medication = new Medication  (resMap);
                                med_list.add(medication);
                            }
                        for (QueryDocumentSnapshot documentSnapshot: q_app){
                            HashMap<String,Object> resMap = (HashMap)documentSnapshot.getData();


                                String status = resMap.get("status").toString();
                                switch(status){
                                    case "Finished":{
                                    //case "Finished":{
                                        timeline_item apps = new timeline_item(resMap);

//                                        add to medlist for one applicable
                                    for (int i=g_index; i<med_list.size(); i++){
                                        Medication   cur_med = med_list.get(i);

                                        int start_date = cur_med.getintInitDate();
                                        int end_date;
                                     //   if (cur_med.getStatus().equals("complete")){
                                        if (cur_med.getStatus().equals("past")){
                                            end_date = cur_med.getintEndDate();
                                        }else{
                                            end_date = (int) (System.currentTimeMillis()/1000);
                                        }

                                        if (start_date > apps.getDate()){
//                                                start date after most recent app
                                            g_index++;
                                        }else{
                                            if (end_date >= apps.getDate()){
//                                                    end after or equal to date
//                                                    on medication
                                                apps.addMeds(cur_med.getMedicine());
                                            }else{
//                                                    end before date
//                                                    do nothing
                                                    ;
                                            }
                                        }
                                    }

//                                         add to app_list;
                                    tl_list.add(apps);
                                    break;
                                }
                                default:{
                                    break;
                                }
                            }
                        }
                        dataloadedListener.onDataLoaded();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Timeline.TAG, "Load appointment and meds in repo failed");
                    }
                });

    }
}
