package com.example.uhealth.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.uhealth.Activity.ShareFeature;
import com.example.uhealth.Activity.Timeline;
import com.example.uhealth.Adapters.rvadapter_shareoutstandings;
import com.example.uhealth.R;
import com.example.uhealth.ViewModel.Share_ViewModel;
import com.example.uhealth.utils.FireBaseInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

public class ShareRequest_Frag extends Fragment {
    private final String OUTSTANDING = "Outstanding_Requests";
    private final String ACCEPTED  = "Accepted_Requests";
    private final String USERS = "users";

    private ProgressDialog progressDialog;

    private Context mContext;
    private FrameLayout requestFrame, viewFrame;
    private RelativeLayout requestLayout, viewLayout;
    private RecyclerView mRV;
    private RecyclerView.LayoutManager layoutManager;
    private rvadapter_shareoutstandings mRVAdapter;
    private EditText mEmail;
    private Button mSubmit;
    private FireBaseInfo mFireBaseInfo;
    private String ownId;
    private String targetId;
    private String idKey;
    private String mailname ="";
    private String username ="";
    private String tomailname = "";
    private String tousername = "";
    private Share_ViewModel viewModel;


    public ShareRequest_Frag() {
        // Required empty public constructor
        mFireBaseInfo = new FireBaseInfo();
        ownId = mFireBaseInfo.mUser.getUid();
    }

    public static ShareRequest_Frag newInstance() {
        ShareRequest_Frag fragment = new ShareRequest_Frag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFireBaseInfo.mFirestore.collection(USERS).document(mFireBaseInfo.mUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            mailname = documentSnapshot.getData().get("email").toString();
                            username = documentSnapshot.getData().get("username").toString();
                        }
                        else{
                            Toast.makeText(getActivity(), "Load user email and username failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share_request_, container, false);
        initView(view);
        initEvents();
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        layoutManager = new GridLayoutManager(mContext,1);
        mRV.setLayoutManager(layoutManager);
        mRVAdapter = new rvadapter_shareoutstandings(mContext, viewModel.getOutstandings().getValue());
        mRV.setAdapter(mRVAdapter);
        mRV.setHasFixedSize(false);
    }


    private void initEvents() {
        viewFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFrame.setVisibility(View.GONE);
                requestLayout.setVisibility(View.GONE);

                requestFrame.setVisibility(View.VISIBLE);
                viewLayout.setVisibility(View.VISIBLE);
            }
        });
        requestFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFrame.setVisibility(View.GONE);
                viewLayout.setVisibility(View.GONE);

                requestLayout.setVisibility(View.VISIBLE);
                viewFrame.setVisibility(View.VISIBLE);
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrapMakeRequest();
            }
        });
    }

    private void wrapMakeRequest() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

//        onsuccess, ownId, targetId and Idkey are all complete updated in this function
//        email, toemail and username, tousername are also updated and only accessed in makeRequest
        String email = mEmail.getText().toString();

        if (email.equals("")){
            mEmail.setError("Empty Email");
            progressDialog.dismiss();
            return;
        }
        if(email.equals(mFireBaseInfo.mUser.getEmail())){
            mEmail.setError("Cannot use your own email");
            progressDialog.dismiss();
            return;
        }

        Task checkEmail = mFireBaseInfo.mAuth.fetchSignInMethodsForEmail(email);
        Task getToId = mFireBaseInfo.mFirestore.collection(USERS)
                .whereEqualTo("email", email)
                .get();

        Task combinedTask = Tasks.whenAllComplete(checkEmail, getToId)
                .addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                    @Override
                    public void onSuccess(List<Task<?>> tasks) {
                        int count=0;
                        for (Task task:tasks){
                            if (!task.isSuccessful() && count==0){
                                mEmail.setError("Invalid Email");
                                progressDialog.dismiss();
                                return;
                            }else if (!task.isSuccessful() && count!=0){
                                Log.d(ShareFeature.TAG, "getId failed");
                                progressDialog.dismiss();
                                return;
                            }
                            count++;
                        }
//                        get uid from email, should only be one and make idkey
//                        check if new user condition is satisfied E.g., not registered
                        boolean nUser = ((SignInMethodQueryResult)(tasks.get(0).getResult())).getSignInMethods().isEmpty();
                        if (nUser){
                            mEmail.setError("Invalid Email");
                            progressDialog.dismiss();
                            return;
                        }
                        QuerySnapshot user = (QuerySnapshot) tasks.get(1).getResult();
                        if(user.size()!=1){
                            Log.d(ShareFeature.TAG, "More than one user has this email, should never happen");
                            progressDialog.dismiss();
                            return;
                        };
//                        Since only 1 item
                        for (QueryDocumentSnapshot documentSnapshot: user){
                            targetId = documentSnapshot.getId();
                            tomailname = documentSnapshot.getData().get("email").toString();
                            tousername = documentSnapshot.getData().get("username").toString();
                            idKey = ownId+targetId;
                        }
//                        check if duplicate in outstanding and accepted
                        Task checkOutstandingRequest = mFireBaseInfo.mFirestore.collection(OUTSTANDING)
                                .whereEqualTo("IdKey", idKey)
                                .get();
                        Task checkAcceptedRequest = mFireBaseInfo.mFirestore.collection(ACCEPTED)
                                .whereEqualTo("IdKey", idKey)
                                .get();
                        Task combinedTask2 = Tasks.whenAllComplete(checkOutstandingRequest, checkAcceptedRequest)
                                .addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                                    @Override
                                    public void onSuccess(List<Task<?>> tasks) {
                                        for (Task task:tasks){
                                            if (!task.isSuccessful()){
                                                Log.d(ShareFeature.TAG, "one of the check request query failed");
                                                return;
                                            }
                                        }
                                        QuerySnapshot outstandings = (QuerySnapshot) tasks.get(0).getResult();
                                        QuerySnapshot accpeteds = (QuerySnapshot) tasks.get(1).getResult();
                                        if (outstandings.size()+accpeteds.size()>0){
                                            mEmail.setError("An request to this user has been made");
                                            progressDialog.dismiss();
                                            return;
                                        }else{
//                                            continue making the request
                                            makeRequest();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(ShareFeature.TAG, "Check request query failed");
                                        progressDialog.dismiss();
                                        return;
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(ShareFeature.TAG, "combined check emails failed");
                        progressDialog.dismiss();
                        return;
                    }
                });
    }

    private void makeRequest() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("IdKey", idKey);
        map.put("from_Id", ownId);
        map.put("to_Id", targetId);
        map.put("expire", System.currentTimeMillis()/1000+((ShareFeature)mContext).OUTSTANDING_EXPIRE_TIME);
        map.put("from_email", mailname);
        map.put("from_username", username);
        map.put("to_email", tomailname);
        map.put("to_username", tousername);
        mFireBaseInfo.mFirestore.collection(OUTSTANDING)
                .add(map)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(mContext, "Make Request Success", Toast.LENGTH_SHORT).show();
                            mEmail.setText("");
                            return;
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(mContext, "Make Request Failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }


    private void initView(View view) {
        requestLayout = view.findViewById(R.id.fsv_request_requestsection);
        viewFrame = view.findViewById(R.id.fsv_request_viewframesection);
        requestFrame = view.findViewById(R.id.fsv_request_requestframesection);
        viewLayout = view.findViewById(R.id.fsv_request_viewsection);
        mEmail = view.findViewById(R.id.fsv_enter_ET);
        mSubmit = view.findViewById(R.id.fsv_enter_bt);
        mRV = view.findViewById(R.id.fsv_request_rv);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context.getClass() == ShareFeature.class){
            mContext = context;
            viewModel = new ViewModelProvider(getActivity()).get(Share_ViewModel.class);
        }
    }

    public void updateadapter(){
        mRVAdapter.notifyDataSetChanged();
    }
}