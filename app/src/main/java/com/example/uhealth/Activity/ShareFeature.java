package com.example.uhealth.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.uhealth.Adapters.FPAdapter_Share;
import com.example.uhealth.DataModel.Share_accepted_item;
import com.example.uhealth.DataModel.Share_outstandings_item;
import com.example.uhealth.Fragments.Outstanding_accept_Dfrag;
import com.example.uhealth.Fragments.ShareRequest_Frag;
import com.example.uhealth.Fragments.ShareView_frag;
import com.example.uhealth.Interfaces.DataloadedListener;
import com.example.uhealth.Interfaces.ShareDataLoadedListener;
import com.example.uhealth.R;
import com.example.uhealth.ViewModel.Share_ViewModel;
import com.example.uhealth.ViewModelFactory.ShareViewModelFactory;
import com.example.uhealth.utils.FireBaseInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ShareFeature extends AppCompatActivity {
    public static final String TAG = ShareFeature.class.getSimpleName();
    private final String OUTSTANDINGS = "Outstanding_Requests";
    private final String ACCEPTED = "Accepted_Requests";
    private final String APPOINTMENTS = "AAppointment";
    private final String INFOHISTORY = "personalInfo";
    private final String DIAGNOSIS = "PastDiagnosis";

    private final String AR_TIMELINE = "SharedTimeline";
    private final String AR_INFOHISTORY = "SharedInfoHistory";
    private final String AR_DIAGNOSIS="SharedDiagnosis";
//    in s
    public static final long OUTSTANDING_EXPIRE_TIME = 24*3600;
    public static final long ACCEPTED_EXPIRE_TIME = 24*3600;
//    in ms
    public static final int LOOP_INTERVAL = 30*1000;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FPAdapter_Share fragmentPagerAdapter;
    private FireBaseInfo mFireBaseInfo;
    private Share_ViewModel viewModel;

    private ProgressDialog progressDialog;

    private Handler mHandler;
    private Runnable mLoopcheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_feature);

        getSupportActionBar().setTitle("Sharing Feature");
        mFireBaseInfo = new FireBaseInfo();

        initView();
        initViewPager();

        ShareViewModelFactory factory = new ShareViewModelFactory(new ShareDataLoadedListener() {
            @Override
            public void onOutstandingsLoaded() {
                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.sf_viewpager + ":" + 0);
                ShareRequest_Frag mFragRequest = (ShareRequest_Frag) page;
                viewModel.getOutstandings().observe(ShareFeature.this, new Observer<List<Share_outstandings_item>>() {
                    @Override
                    public void onChanged(List<Share_outstandings_item> share_outstandings_items) {
                        if (mFragRequest!=null){
                            mFragRequest.updateadapter();
//                            Toast.makeText(ShareFeature.this, "Outstandings reloaded", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
            @Override
            public void onAcceptedLoaded() {
                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.sf_viewpager + ":" + 1);
                ShareView_frag mFragView = (ShareView_frag) page;
                viewModel.getAccepteds().observe(ShareFeature.this, new Observer<List<Share_accepted_item>>() {
                    @Override
                    public void onChanged(List<Share_accepted_item> share_accepted_items) {
                        if (mFragView!=null){
                            mFragView.updateAdapter();
//                            Toast.makeText(ShareFeature.this, "Accepted reloaded", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
        viewModel = new ViewModelProvider(this, factory).get(Share_ViewModel.class);

        initHandler();
    }

    private void initHandler() {
//        use of handler is justified since firebase is already an Async task, check work is light
        mHandler = new Handler(Looper.getMainLooper());
        mLoopcheck = new Runnable() {
            @Override
            public void run() {
                long cur_time = System.currentTimeMillis()/1000;
//                both are oldest first
                if (viewModel!=null){
                    for(Share_outstandings_item item :viewModel.getOutstandings().getValue()){
                        if (item.getExpire()<cur_time){
                            String id = item.getDocumentId();
                            ShareFeature.this.deleteOutstandingdoc(id);
                        }else{
                            break;
                        }
                    }
                    for(Share_accepted_item item: viewModel.getAccepteds().getValue()){
                        if (item.getExpire()<cur_time){
                            String id = item.getDocumentId();
                            ShareFeature.this.deleteAccepteddoc(id);
                        }else{
                            break;
                        }
                    }
                }
                Log.d(TAG, "One loop of expire check");
                mHandler.postDelayed(this, LOOP_INTERVAL);
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.post(mLoopcheck);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mLoopcheck);
    }

    private void initViewPager() {
        fragmentPagerAdapter = new FPAdapter_Share(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        fragmentPagerAdapter.addFragments(ShareRequest_Frag.newInstance(), "Request");
        fragmentPagerAdapter.addFragments(ShareView_frag.newInstance(), "View");

        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initView() {
        tabLayout = findViewById(R.id.sf_tablayout);
        viewPager = findViewById(R.id.sf_viewpager);
    }

    public void deleteOutstandingdoc(String id){
        mFireBaseInfo.mFirestore.collection(OUTSTANDINGS).document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Delete outstanding success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShareFeature.this, "Delete Outstanding Failed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Delete outstanding failed");
            }
        });
    }

    public void deleteAccepteddoc(String id){
        mFireBaseInfo.mFirestore.collection(ACCEPTED).document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Delete Accepted success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShareFeature.this, "Delete Accepted Failed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Delete Accepted failed");
            }
        });
    }

    public void openOutstandingAcceptDialog(Share_outstandings_item in_item){
        viewModel.setOutstandingAcceptDialogItem(in_item);
        Outstanding_accept_Dfrag outstandingAcceptDfrag = Outstanding_accept_Dfrag.newInstance();
        outstandingAcceptDfrag.show(getSupportFragmentManager(), "tag");
    }



    //    todo cutcorner complete
    public void acceptOutstandings(HashMap<String, Boolean> b_map){

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


//        item is saved into viewmodel, thus even if outstanding is deleted, the dialog can still generate desired result
        Share_outstandings_item item = viewModel.getOutstandingAcceptDialogItem();
        HashMap<String, Object> map = new HashMap<>();
        map.put("IdKey", item.getIdKey());
        map.put("expire", System.currentTimeMillis()/1000+ACCEPTED_EXPIRE_TIME);
        map.put("from_Id", item.getFrom_id());
        map.put("from_email", item.getFrom_email());
        map.put("from_username", item.getFrom_username());
        map.put("to_Id", item.getTo_id());
        map.put("to_email", item.getTo_email());
        map.put("to_username", item.getTo_username());

        final String to_uid = item.getTo_id();
        final boolean mInfoandHistory, mDiagnosis, mTimeline;
        final String outstandingdocid = item.getDocumentId();
        mInfoandHistory = b_map.get("InfoandHistory");
        mDiagnosis = b_map.get("Diagnosis");
        mTimeline = b_map.get("Timeline");

        map.put("InfoandHistory", mInfoandHistory);
        map.put("Diagnosis", mDiagnosis);
        map.put("Timeline", mTimeline);


        mFireBaseInfo.mFirestore.collection(ACCEPTED)
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

//                        newly created accepted item reference
//                        Firebase query is shallow, nested collection is accepted (does not surface on query)
                        final String docid = documentReference.getId();

                        List<Task<?>> taskList= new ArrayList<>();

//                        todo instead of taking info snapshot, going to be direct load
                        if (mInfoandHistory){
//                            Task<?> addInfoandHistory = mFireBaseInfo.mFirestore.collection("users")
//                                    .whereEqualTo("username", "test")
//                                    .get()
//                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                            for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
//                                                mFireBaseInfo.mFirestore.collection(ACCEPTED).document(docid)
//                                                        .collection("testcollection")
//                                                        .add(documentSnapshot.getData());
//                                            }
//                                        }
//                                    });
//                            taskList.add(addInfoandHistory);
                        }
                        if (mDiagnosis){
//                            Task = ...
//                            taskList.add();
                        }
                        if(mTimeline){
//                            Task<?> addTimeline = mFireBaseInfo.mFirestore
//                                    .collection(APPOINTMENTS)
//                                    .whereEqualTo("patientid", to_uid)
//                                    .get()
//                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                            for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
//                                                mFireBaseInfo.mFirestore.collection(ACCEPTED).document(docid)
//                                                        .collection("testtimeline")
//                                                        .add(documentSnapshot.getData());
//                                            }
//                                        }
//                                    });
//                            taskList.add(addTimeline);
                        }

//                        Tasks.whenAllComplete(taskList)
//                                .addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
//                                    @Override
//                                    public void onSuccess(List<Task<?>> tasks) {
//                                        for (Task task:tasks){
//                                            if (!task.isSuccessful()){
//                                                progressDialog.dismiss();
//                                                Log.d(Timeline.TAG, "Least one task failed in moving into accepted instance");
//                                                return;
//                                            }
//                                        }
                                        mFireBaseInfo.mFirestore.collection(OUTSTANDINGS).document(outstandingdocid)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progressDialog.dismiss();
                                                        Log.d(TAG, "delete old outstanding succeeded");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Log.d(TAG, "delete old outstanding failed");
                                                        Toast.makeText(ShareFeature.this, "Delete old outstanding failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        progressDialog.dismiss();
//                                        Toast.makeText(ShareFeature.this, "Moving Data into Accept Instance failed", Toast.LENGTH_SHORT).show();
//                                        Log.d(TAG, "Moving date into accept instance failed");
//                                    }
//                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShareFeature.this, "Accept Instance failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Create accept instance failed");
                        progressDialog.dismiss();
                    }
                });

    }

//    todo cutcorner complete
    public void viewFromAccepteds(String rbText, Share_accepted_item item){
        String searchuid = item.getTo_id();
        String searchname = item.getTo_username();
        switch (rbText){
            case "Personal Info and Medical History":
                Intent intent = new Intent(this, InfoHistoryPage.class);
                intent.putExtra("FromProfilePage", false);
                intent.putExtra("UID", searchuid);
                intent.putExtra("NAME", searchname);
                startActivity(intent);
                break;
            case "Past Diagnosis":
                Intent intent1 = new Intent(this, PastDiagnosis.class);
                intent1.putExtra("FromInfoHistory", false);
                intent1.putExtra("UID", searchuid);
                intent1.putExtra("NAME", searchname);
                startActivity(intent1);
                break;
            case "Timeline":
                Intent intent2 = new Intent(this, Timeline.class);
                intent2.putExtra("FromProfilePage", false);
                intent2.putExtra("UID", searchuid);
                intent2.putExtra("NAME", searchname);
                startActivity(intent2);
                break;
            default:
                Log.d(TAG, "viewFromAccepteds: missed switch, should never happen");
                break;
        }
    }
}