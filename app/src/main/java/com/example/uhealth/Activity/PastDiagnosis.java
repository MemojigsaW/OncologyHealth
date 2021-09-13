package com.example.uhealth.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.uhealth.Adapters.rvadapter_diag;
import com.example.uhealth.Interfaces.DataloadedListener;
import com.example.uhealth.R;
import com.example.uhealth.ViewModel.PastDiagnosis_ViewModel;
import com.example.uhealth.ViewModelFactory.PastDiagnosisViewModelFactory;
import com.example.uhealth.utils.FireBaseInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

public class PastDiagnosis extends AppCompatActivity {
    public static final String TAG = PastDiagnosis.class.getSimpleName();
    public final String PASTDIAGNOSIS = "PastDiagnosis";

    private FireBaseInfo mFireBaseInfo;
    private boolean fromInfoHistory;
    private String uidKey;

    private RecyclerView RVdropdown;
    private RecyclerView.LayoutManager layoutManagerdropdown;
    private rvadapter_diag mAdapter;
    private Button mSubmit;

    private PastDiagnosis_ViewModel viewModel;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_diagnosis);

        mFireBaseInfo = new FireBaseInfo();

        initview();

        Intent intent = getIntent();
        fromInfoHistory = intent.getBooleanExtra("FromInfoHistory", true);
        if (fromInfoHistory){
            uidKey = mFireBaseInfo.mUser.getUid();
            getSupportActionBar().setTitle("Past Diagnosis");
        }else{
            uidKey = intent.getStringExtra("UID");
            String name = intent.getStringExtra("NAME");
            getSupportActionBar().setTitle(name+"'s Diagnosis History");
            mSubmit.setVisibility(View.GONE);
        }

        initEvent();
        initRV();
        initVM();

    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    private Intent getParentActivityIntentImpl() {
        Intent i = null;

        // Here you need to do some logic to determine from which Activity you came.
        // example: you could pass a variable through your Intent extras and check that.
        if (fromInfoHistory) {
            i = new Intent(this, ProfilePage.class);
            // set any flags or extras that you need.
            // If you are reusing the previous Activity (i.e. bringing it to the top
            // without re-creating a new instance) set these flags:
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            i.putExtra("someExtra", "whateverYouNeed");
        } else {
            i = new Intent(this, ShareFeature.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            i.putExtra("someExtra", "whateverYouNeed");
        }
        return i;
    }

    private void initVM() {
        PastDiagnosisViewModelFactory factory = new PastDiagnosisViewModelFactory(fromInfoHistory, uidKey, new DataloadedListener() {
            @Override
            public void onDataLoaded() {
                progressDialog.dismiss();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onBeforeLoaded() {
                progressDialog.show();
            }
        });
        viewModel = new ViewModelProvider(this, factory).get(PastDiagnosis_ViewModel.class);
    }

    private void initRV() {
        layoutManagerdropdown = new GridLayoutManager(this, 1);
        String[] categorylist = getResources().getStringArray(R.array.Diag_Category);
        mAdapter = new rvadapter_diag(this, categorylist);
        RVdropdown.setLayoutManager(layoutManagerdropdown);
        RVdropdown.setAdapter(mAdapter);
        RVdropdown.setHasFixedSize(false);
    }

    private void initview() {
        RVdropdown = findViewById(R.id.diag_dropdownRV);
        mSubmit = findViewById(R.id.pd_submit_BT);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void initEvent(){
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitInfo();
            }
        });
    }

    private void submitInfo() {
        Map<String, Object> mp = new HashMap<>(viewModel.getDiag_objMutableLiveData().getValue().getDiseases_others());
        mp.put("Diseases", viewModel.getDiag_objMutableLiveData().getValue().getAll_Diseases());
        mFireBaseInfo.mFirestore.collection(PASTDIAGNOSIS).document(mFireBaseInfo.mUser.getUid())
                .set(mp)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PastDiagnosis.this, "Submission Successful", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Submit Diagnosis Form Failed");
                    }
                });
    }


    //    viewmodel accesses, should be through an interface, cutted corners...
    public boolean containsDisease(String obj){
        return viewModel.getDiag_objMutableLiveData().getValue().containsDisease(obj);
    }
    public void addDisease(String obj){
        viewModel.getDiag_objMutableLiveData().getValue().addDisease(obj);
    }
    public void rmDisease(String obj){
        viewModel.getDiag_objMutableLiveData().getValue().rmDisease(obj);
    }
    public String getOthers(String key){
        return viewModel.getDiag_objMutableLiveData().getValue().getOthers(key);
    }
    public void putOthers(String key, String content){
        viewModel.getDiag_objMutableLiveData().getValue().setOthers(key, content);
    }
    public boolean containsPosition(int pos){
        return viewModel.containsPosition(pos);
    }
    public void addPosition(int pos){
        viewModel.addPosition(pos);
    }
    public void rmPosition(int pos){
        viewModel.rmPosition(pos);
    }
}