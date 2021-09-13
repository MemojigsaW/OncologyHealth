package com.example.uhealth.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uhealth.Adapters.FSPadapter_healthtip;
import com.example.uhealth.AppointmnetList;
import com.example.uhealth.utils.FireBaseInfo;
import com.example.uhealth.MedicationList;
import com.example.uhealth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfilePage extends AppCompatActivity {
    private FireBaseInfo mFireBaseInfo;
    private Toolbar toolbar;
    private TextView mUsername;

    private ViewPager viewPager;
    private String[] tiplist;
    private FSPadapter_healthtip mVPAdapter;

    private Handler mHandler;
    private Runnable mLoopRun;
    private int mInterval = 8*1000;

    private RelativeLayout mPlaceHolderInitReg, mPlaceHolderapp, mPlaceHoldermed, mPlaceHolderQuestionnaire,
            mPlaceHolderTimeline, mPlaceHolderShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        mFireBaseInfo = new FireBaseInfo();
        toolbar = findViewById(R.id.profilepg_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        mUsername = findViewById(R.id.profileusername);

        mPlaceHolderInitReg = findViewById(R.id.infopageSection);

        mPlaceHolderQuestionnaire = findViewById(R.id.questionnaireSection);

        mPlaceHolderapp = findViewById(R.id.appointmentSection);

        mPlaceHoldermed = findViewById(R.id.medicationSection);

        mPlaceHolderTimeline = findViewById(R.id.TimeLineSection);

        mPlaceHolderShare = findViewById(R.id.ShareSection);

        initVP();
        initHandler();

    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper());
        mLoopRun = new Runnable() {
            @Override
            public void run() {
                int cur_index = viewPager.getCurrentItem();
                if (cur_index==tiplist.length+1){
                    viewPager.setCurrentItem(0);
                }else{
                    viewPager.setCurrentItem(cur_index+1);
                }
                mHandler.postDelayed(mLoopRun, mInterval);
            }
        };
    }


    private void initVP() {
        viewPager = findViewById(R.id.VP_healthtip);
        tiplist = getResources().getStringArray(R.array.Preventative_Health);
        FragmentManager fragmentManager = getSupportFragmentManager();

        mVPAdapter = new FSPadapter_healthtip(fragmentManager,
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                tiplist);
        viewPager.setAdapter(mVPAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == tiplist.length){
                    viewPager.setCurrentItem(0, false);
                }
                mHandler.removeCallbacks(mLoopRun);
                mHandler.postDelayed(mLoopRun, mInterval);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlaceHolderInitReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, InfoHistoryPage.class);
                intent.putExtra("FromProfilePage", true);
                startActivity(intent);
            }
        });
        mPlaceHolderQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, Questionnaire.class);
                startActivity(intent);
            }
        });
        mPlaceHolderapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, AppointmnetList.class);
                startActivity(intent);
            }
        });
        mPlaceHoldermed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, MedicationList.class);
                startActivity(intent);
            }
        });
        mPlaceHolderTimeline.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, Timeline.class);
                intent.putExtra("FromProfilePage", true);
                startActivity(intent);
            }
        });
        mPlaceHolderShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, ShareFeature.class);
                startActivity(intent);
            }
        });

        mFireBaseInfo.mFirestore.collection("users").document(mFireBaseInfo.mUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            try {
                                mUsername.setText(documentSnapshot.get("username").toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(ProfilePage.this, "load username failed", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

        mHandler.postDelayed(mLoopRun, mInterval);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mLoopRun);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        adds to action bar if present
        inflater.inflate(R.menu.profilepg_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int v_id = item.getItemId();
        if (v_id==R.id.pp_tb_setting){
            Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
            return true;
        }else if (v_id == R.id.pp_tb_logout){
            Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
            logOut();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void logOut() {
        mFireBaseInfo.mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}