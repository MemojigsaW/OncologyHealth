package com.example.uhealth.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.uhealth.Fragments.Frag_questionnaire_details;
import com.example.uhealth.R;

public class QuestionnaireResults extends AppCompatActivity {
    public static final String TAG = QuestionnaireResults.class.getSimpleName();

    private RelativeLayout mScorelayout;
    private TextView mScoretitle;
    private TextView mScore;
    private FrameLayout mFragContainer;
    private String mTitleAddOn = "";
    private String mTitle ="";

    private Fragment mFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_results);

        initView();

        Intent intent = getIntent();
        String qid = intent.getStringExtra("QID");
        String description, interpretation, further;

        switch (qid){
            case "058oX2H0lvHLyYFXQMls":
                mTitleAddOn = "Eastern Cooperative Oncology Group Scale (ECOG)";
                mTitle = "ECOG";
                description = getResources().getString(R.string.ECOG_Description);
                interpretation = getResources().getString(R.string.ECOG_Interpretation);
                further = getResources().getString(R.string.ECOG_Furthermore);
                break;
            case "M7ynd1hCO7Tw2Wi2v3Jt":
                mTitleAddOn = "The International Consultation on Incontinence (ICIQ)";
                mTitle = "ICIQ";
                description = getResources().getString(R.string.ICIQ_Description);
                interpretation = getResources().getString(R.string.ICIQ_Interpretation);
                further = getResources().getString(R.string.ICIQ_Furthermore);
                break;
            case "WVsMwi0gL8VlANuPDt97":
                mTitleAddOn = "International Index of Erectile Function (IIEF)";
                mTitle = "IIEF";
                description = getResources().getString(R.string.IIEF_Description);
                interpretation = getResources().getString(R.string.IIEF_Interpretation);
                further = getResources().getString(R.string.IIEF_Furthermore);
                break;
            default:
                Log.d(TAG, "switch statement misssed, should never happen");
                description = "";
                interpretation="";
                further="";
                break;
        }

        getSupportActionBar().setTitle(mTitle+" Information");
        mFragment = new Frag_questionnaire_details(mTitleAddOn, description, interpretation, further);


        try{
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_result, mFragment);
            ft.commit();
        }catch (Exception e){
            e.printStackTrace();
        }

        int score = intent.getIntExtra("SCORE", 0);
        mScoretitle.append(mTitleAddOn);
        mScore.setText(Integer.toString(score));
        if (intent.getBooleanExtra("HASSCORE",false)){
            mScorelayout.setVisibility(View.VISIBLE);
        }else{
            mScorelayout.setVisibility(View.GONE);
        }

    }

    private void initView() {
        mScorelayout = findViewById(R.id.score_layout);
        mScoretitle = findViewById(R.id.scoretitle);
        mScore = findViewById(R.id.actualscore);
        mFragContainer = findViewById(R.id.fragment_result);
    }
}