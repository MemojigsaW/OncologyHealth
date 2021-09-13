package com.example.uhealth.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.uhealth.utils.FireBaseInfo;
import com.example.uhealth.R;
import com.example.uhealth.Adapters.rvadapter_questionnaire_questions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Questionnaire extends AppCompatActivity {
    public static final String TAG = Questionnaire.class.getSimpleName();
    private FireBaseInfo mFireBaseInfo;

    private RecyclerView mRVquestionsets;
    private RecyclerView.LayoutManager mLMquestionsets;
    private rvadapter_questionnaire_questions mADquestionsets;
    private Button mStartButton;
    private RelativeLayout mStartLayout;

    private String qs_idHolder= "", qs_textHolder="", qs_descripHolder="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        mFireBaseInfo = new FireBaseInfo();

        mRVquestionsets = findViewById(R.id.questionnaire_RV_questionnaires);
        mLMquestionsets = new GridLayoutManager(this, 1);
        mRVquestionsets.setLayoutManager(mLMquestionsets);
        mStartLayout = findViewById(R.id.questionnaire_startlayout);
        mStartButton = mStartLayout.findViewById(R.id.questionnaire_start);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartListener();
            }
        });

        setRV();
        getSupportActionBar().setTitle("Questionnaire");
    }

    private void setStartListener() {
        if (qs_textHolder.equals("") || qs_idHolder.equals("")){
            Toast.makeText(this, "Need to Select a Question Set", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(Questionnaire.this, Do_questions.class);
            intent.putExtra("qs_id", qs_idHolder);
            intent.putExtra("qs_text", qs_textHolder);
            Questionnaire.this.startActivity(intent);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        qs_idHolder = savedInstanceState.getString("QS_ID");
        qs_textHolder = savedInstanceState.getString("QS_TEXT");
        qs_descripHolder = savedInstanceState.getString("QS_DES");
        mStartLayout.setVisibility(savedInstanceState.getInt("START"));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("QS_ID", qs_idHolder);
        outState.putString("QS_TEXT", qs_textHolder);
        outState.putString("QS_DES", qs_descripHolder);
        outState.putInt("START", mStartLayout.getVisibility());
        super.onSaveInstanceState(outState);
    }

    private void setRV() {
        mFireBaseInfo.mFirestore.collection("questionnaire").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int total = queryDocumentSnapshots.size();
                        LinkedHashMap<String, Map<String, String>> mp = new LinkedHashMap<>(total);
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                            String mp_name = document.getData().get("name").toString();
                            String mp_des = document.getData().get("description").toString();
                            String key = document.getId();
                            Map<String,String> v_mp = new HashMap<>();
                            v_mp.put("name", mp_name);
                            v_mp.put("des", mp_des);
                            mp.put(key, v_mp);
                        }
                        mADquestionsets = new rvadapter_questionnaire_questions(Questionnaire.this, mp, new rvadapter_questionnaire_questions.onQSClickListener() {
                            @Override
                            public void onQSClick(String qs_id, String qs_text, String qs_des) {
                                Questionnaire.this.qs_idHolder = qs_id;
                                Questionnaire.this.qs_textHolder = qs_text;
                                Questionnaire.this.qs_descripHolder = qs_des;
                                mStartLayout.setVisibility(View.VISIBLE);
                            }
                        });
                        mRVquestionsets.setAdapter(mADquestionsets);
                        mRVquestionsets.setHasFixedSize(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Fail to retrive questionnaire from firebase");
                    }
                });
    }


}