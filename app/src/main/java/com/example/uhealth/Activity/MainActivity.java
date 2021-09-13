package com.example.uhealth.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.uhealth.utils.FireBaseInfo;
import com.example.uhealth.R;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private FireBaseInfo mFireBaseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        mFireBaseInfo = new FireBaseInfo();
        FirebaseUser mUser = mFireBaseInfo.mUser;

        Intent intent;
        if (mUser != null){
            Log.d(TAG, "User loggin on mainactivity");
            intent = new Intent(this, ProfilePage.class);
        }else{
            Log.d(TAG, "User not loggedin on mainactivity");
            intent = new Intent(this, LoginPage.class);
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
    @Override
    protected void onStart(){
        super.onStart();
//        createNotificationChannel();
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel")
//                .setSmallIcon(R.drawable.bg_round)
//                .setContentTitle("test")
//                .setContentText("testcontent")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//
//// notificationId is a unique int for each notification that you must define
//        notificationManager.notify(42, builder.build());
    }

//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "asdchannel";
//            String description = "channel des";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("channel", name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
}