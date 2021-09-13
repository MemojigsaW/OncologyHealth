package com.example.uhealth.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.uhealth.R;
import com.example.uhealth.utils.FireBaseInfo;
import com.example.uhealth.utils.GlideApp;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;

public class DisplayFullImage extends AppCompatActivity {
    private String image_path;
    private FireBaseInfo mFireBaseInfo;
    private ImageView mPhoto;
    private FloatingActionButton mFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_full_image);

        Intent receive = getIntent();
        image_path = receive.getStringExtra("IMAGE_PATH");

        mFireBaseInfo = new FireBaseInfo();


        initView();
        initTrigger();
    }


    private void initTrigger() {
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        mPhoto = findViewById(R.id.fullscreenphoto);
        mFAB = findViewById(R.id.FAB_Cancel_fullscreen);

        StorageReference pathRef = mFireBaseInfo.mStorageRef.child(image_path);

        GlideApp.with(this)
                .load(pathRef)
                .thumbnail(
                        Glide.with(this)
                        .load(getResources().getIdentifier("ic_thumbnail_bg_foreground",
                                "drawable", this.getPackageName()))
                )
                .apply(new RequestOptions().override(mPhoto.getWidth(), mPhoto.getHeight()))
                .into(mPhoto);
    }
}