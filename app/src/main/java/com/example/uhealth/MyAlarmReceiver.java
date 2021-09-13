package com.example.uhealth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.uhealth.Activity.ProfilePage;

public class MyAlarmReceiver extends BroadcastReceiver {
    private static final int APPOINTMENT_CHANNEL = 5;
    private static final int MEDICATION_CHANNEL = 6;
    AlertDialog mAlarmAppointmentDialog;
    private NotificationManager m_notificationMgr = null;
    private static final int NOTIFICATION_FLAG = 3;
    final private String Cid = "mChannelID";
    public void initChannel(Context context){
        //Notification.Builder builder;
        m_notificationMgr = (NotificationManager) context.getSystemService(NotificationManager.class);
        NotificationChannel channel = new NotificationChannel(Cid, Cid,
                NotificationManager.IMPORTANCE_DEFAULT);
        //builder = new Notification.Builder(context, Cid);
        m_notificationMgr.createNotificationChannel(channel);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //String AppointmentType = intent.getStringExtra("type");
        //

       // createNotificationChannel(context,m_notificationMgr);
        initChannel(context);
        Intent starter = intent;
        Bundle extras = starter.getExtras();
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        if (extras != null) {
            boolean isAppointment = extras.getBoolean("isAppointment", false);
            boolean isMedication= extras.getBoolean("isMedication", false);
            if (isAppointment) {
                Intent ListIntent = new Intent(context, AppointmnetList.class);
                ListIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ListIntent.putExtra("isAppointment",true);
                ListIntent.putExtra("typefromalarm","AppointmentReminder");
                ListIntent.putExtra("apttype",extras.getString("apttype"));
                ListIntent.putExtra("apttime",extras.getString("apttime"));
                ListIntent.putExtra("position",extras.getInt("position"));
                stackBuilder.addParentStack(AppointmnetList.class);
                stackBuilder.addNextIntent(ListIntent);
                PendingIntent pendingIntent =stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
               // PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ListIntent, 0);
                NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context,Cid )
                        .setCategory(Notification.CATEGORY_RECOMMENDATION)
                        .setSmallIcon(R.drawable.ic_baseline_favorite_24)
                        .setTicker(extras.getString("apttype"))
                        .setContentTitle("Appointment Reminder!")
                        .setContentText(extras.getString("apttime"))
                        .setContentIntent(pendingIntent)
                        .setNumber(1);
                       // .build();
                Notification notify = notifyBuilder.build();
               // notify.flags |= Notification.FLAG_AUTO_CANCEL;
                m_notificationMgr .notify(NOTIFICATION_FLAG, notify);


                /*-----------------------------------------
                Intent ListIntent = new Intent(context, AppointmnetList.class);
                ListIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ListIntent.putExtra("isAppointment",true);
                ListIntent.putExtra("typefromalarm","AppointmentReminder");
                ListIntent.putExtra("apttype",extras.getString("apttype"));
                ListIntent.putExtra("apttime",extras.getString("apttime"));
                context.startActivity(ListIntent);
                */

            } else {

                //Do nothing
            }

            if(isMedication){
                Intent MedIntent = new Intent(context, MedicationList.class);
                MedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MedIntent.putExtra("isMedication",true);
                MedIntent.putExtra("position",extras.getInt("position"));
                MedIntent.putExtra("medicine",starter.getStringExtra("medicine"));//medicine
                stackBuilder.addParentStack(MedicationList.class);
                stackBuilder.addNextIntent(MedIntent);
                /////////////////////
                PendingIntent mpendingIntent =stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                //PendingIntent mpendingIntent = PendingIntent.getActivity(context, 0, MedIntent, 0);
                Notification mnotify = new Notification.Builder(context,Cid)
                        .setSmallIcon(R.drawable.ic_baseline_favorite_24)
                        .setTicker(extras.getString("medicine"))
                        .setContentTitle("Medication Reminder!")
                        .setContentText(extras.getInt("position")+"."+extras.getString("medicine"))
                        .setContentIntent(mpendingIntent)
                        .setNumber(1)
                        .build();
             //   mnotify.flags |= Notification.FLAG_AUTO_CANCEL;
                m_notificationMgr .notify(NOTIFICATION_FLAG, mnotify);
                ///////////////////////
                //context.startActivity(MedIntent);

            }else{

            }
        }

    }

}
