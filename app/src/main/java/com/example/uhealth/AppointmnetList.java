package com.example.uhealth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;

import com.example.uhealth.Activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppointmnetList extends AppCompatActivity  {
    final  static List <Appointment> AppointmentList = new ArrayList<>();
    AppointmentAdapter appointmentAdapter;
    private AlertDialog mRemoveAppointmentDialog;
    private AlertDialog mAlarmAppointmentDialog;
    Button AppointmentRemover,AppointmentAdder;
    private FireBaseInfo mFireBaseInfo;
    private PowerManager.WakeLock mWakelock;
    final static int REQUEST_ADD_AN_APPOINTMENT = 100;
    final static int SET_APPOINTMENT_ALARM = 15;
    private MediaPlayer mp = new MediaPlayer();
    public void start_adder(){
        Intent addAptIntent = new Intent(this,AppointmentAdder.class);
        startActivityForResult(addAptIntent, REQUEST_ADD_AN_APPOINTMENT);

    }
    public void setAlarm(Date date_time,String atype){
        Toast.makeText(AppointmnetList.this,"Trying to create alarm",Toast.LENGTH_LONG).show();
      //  SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        Date cur = new Date();

       // Log.d("ZXZXZX","TRYING TO SET ALARM");MyAlarmReceiver
        Intent intent = new Intent(AppointmnetList.this, MyAlarmReceiver.class);
        //intent.putExtra("type",atype);
      //  Toast.makeText(AppointmnetList.this,atype,Toast.LENGTH_SHORT).show();
        PendingIntent sender = PendingIntent.getBroadcast(AppointmnetList.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
     //   Calendar appointment_calendar  =Calendar.getInstance();
        //appointment_calendar.setTime(date_time);

      //  appointment_calendar.add(appointment_calendar.DAY_OF_MONTH,-1);

        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
  //      manager.setExact(AlarmManager.RTC_WAKEUP, appointment_calendar.getTimeInMillis(), sender);
        manager.set(AlarmManager.RTC_WAKEUP, cur.getTime()+12*1000, sender);
    }
    public int bubble(Appointment newinstance){
        final Date currentDate = new Date();
        SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        String appointment_time = newinstance.getDate();
        Long newtime =new  Long(0);
        Long oldtime =new Long(0);
        int position = 0;
        final int count = AppointmentList.size();
        if(count <=0){
            AppointmentList.add(newinstance);
        }
        else{
            for(int i =0; i <= count ;i++){
                if(i==count )
                {
                    AppointmentList.add(i,newinstance);
                    position = i;
                }else{
                    int res = 0;
                    try{
                        newtime = mdateformat.parse(appointment_time).getTime();
                    }
                    catch(Exception e){

                    }
                    try{
                        oldtime = mdateformat.parse(AppointmentList.get(i).getDate()).getTime();
                    }catch(Exception e){

                    }
                    if(newtime <oldtime){

                    }
                    else{
                        AppointmentList.add(i,newinstance);
                        position = i;
                        break;
                    }
                }


            }


        }
        return position;

    }
    public void finalDemo(Date date_time,String aptype,int position){
        SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        long time = System.currentTimeMillis();

        Intent intent = new Intent(AppointmnetList.this,MyAlarmReceiver.class);
        intent.putExtra("isAppointment",true);
        intent.putExtra("apttime",mdateformat.format(date_time));
        intent.putExtra("apttype",aptype);
        intent.putExtra("position",position);
        PendingIntent pi = PendingIntent.getBroadcast(AppointmnetList.this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP,date_time.getTime(),pi);
       // Toast.makeText(AppointmnetList.this,"What is this"+mFormat.format(time+5*1000),Toast.LENGTH_SHORT).show();

    }
    public void download_medication_list(){
        String uid = mFireBaseInfo.mUser.getUid();
        mFireBaseInfo.mFirestore.collection("AAppointment").whereEqualTo("patientid",uid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                        if (task.isSuccessful()) {

                            QuerySnapshot query_res =  task.getResult();
                            List<DocumentSnapshot> documents = query_res.getDocuments();
                            for(int i =0; i < documents.size();i++){
                                DocumentSnapshot instance = documents.get(i);

                                HashMap<String,Object> resMap = (HashMap)instance.getData();
                                //
                                Date t_date = new Date();
                                try{
                                    t_date.setTime((long)1000*Integer.valueOf(resMap.get("date").toString()));
                                    resMap.put("date",mdateformat.format(t_date));

                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
                                //
                                String status = resMap.get("status").toString();
                                Appointment mmedication = new Appointment(resMap);
                                switch(status){
                                    case "Scheduled":{

                                        bubble(mmedication );
                                        break;
                                    }
                                    case "Finished":{
                                        int aa = 1;
                                        bubble(mmedication );
                                        break;

                                    }
                                    case "Past":{
                                        int bb = 1;
                                        bubble(mmedication );
                                    }
                                    default:{

                                        break;
                                    }

                                }
                            }
                            appointmentAdapter.notifyDataSetChanged();



                        } else {

                        }
                    }
                });



    }
    public void upload_appointment_instance(HashMap<String,Object> resMap){
        SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        try {


            final Date t_date = new Date();
            final String old_date = resMap.get("date").toString();
            t_date.setTime(mdateformat.parse(old_date).getTime());
            final int int_date = (int) (t_date.getTime() / 1000);
            resMap.put("date", int_date);

            mFireBaseInfo.mFirestore.collection("AAppointment").add(resMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //  Toast.makeText(RegisterActivity.this, "Ouch!!!", Toast.LENGTH_LONG).show();

                        }

                        ;
                    });
            resMap.put("date", old_date);
        }catch(ParseException e){
            e.printStackTrace();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        int position = 0;
        switch (requestCode) {

            case REQUEST_ADD_AN_APPOINTMENT :{
                if (resultCode == RESULT_OK) {
                   // HashMap<String,Object> resMap = (HashMap)data.getSerializableExtra("data_return");
                    HashMap<String,Object> resMap = new HashMap<String,Object>();
                    Toast.makeText(AppointmnetList.this,"Successfuly added",Toast.LENGTH_LONG);
                    resMap.put("date",data.getStringExtra("date"));
                    resMap.put("physicainname",data.getStringExtra("physicainname"));
                    resMap.put("patientid",data.getStringExtra("patientid"));
                    resMap.put("patientname",data.getStringExtra("patientname"));
                    resMap.put("location",data.getStringExtra("location") );
                    resMap.put("type",data.getStringExtra("type") );
                    resMap.put("status",data.getStringExtra("status") );



                    Log.d("HHHH","we are 0001");
                    Appointment temp_appointment = new Appointment(resMap);

                    position = bubble(temp_appointment);
                    appointmentAdapter.notifyDataSetChanged();
                    Log.d("HHHH","we are 0003");
                    try{


                        SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                        Date date_time = mdateformat.parse(resMap.get("date").toString());;
                        final Date currentDate = new Date();

                        if( date_time.getTime() -currentDate.getTime()> 0) {


                            //setAlarm(date_time,resMap.get("type").toString());
                            finalDemo(date_time,resMap.get("type").toString(),position);
                        }


                    }catch(java.text.ParseException e){
                        e.printStackTrace();
                    }


                    upload_appointment_instance(resMap);

                }
            }




            default:{

            }

        }

    }
    private void startMedia() {
        try {
            mp.setDataSource(this,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            mp.prepare();
            //mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void alarmDialog(String AppointmentType,String apttime){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        final  String Title = "Notification of the "+AppointmentType ;
        final String[] item ={"Don't forget your " + AppointmentType+" at "+apttime,};
        alertBuilder.setTitle(Title);
        alertBuilder.setMessage("Don't forget your " + AppointmentType+"at"+apttime);
        alertBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mp.stop();
               mAlarmAppointmentDialog.dismiss();
            }
        });
        mAlarmAppointmentDialog = alertBuilder.create();
        mAlarmAppointmentDialog.show();
            //mAlarmAppointmentDialog
    }
    private void acquireWakeLock() {
        if (mWakelock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass()
                    .getCanonicalName());
            mWakelock.acquire();
        }
    }

    /**
     * 释放锁屏
     */
    private void releaseWakeLock() {
        if (mWakelock != null && mWakelock.isHeld()) {
            mWakelock.release();
            mWakelock = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseWakeLock();
    }
    @Override
    protected void onResume() {
        super.onResume();

        acquireWakeLock();
    }
    @Override
    protected void onStart(){
        super.onStart();
        Intent starter = getIntent();
        int position = 0;
        Bundle extras = starter.getExtras();
        if (extras != null) {
            boolean isAppointment = extras.getBoolean("isAppointment", false);
            if (isAppointment) {
                //-----------------------------------------

                //-----------------------------------------
                //Toast.makeText(AppointmnetList.this,"Welcomeback",Toast.LENGTH_LONG).show();
                startMedia();// Do something
               // download_medication_list();
                position = starter.getIntExtra("position",0);
                Appointment tempappointment = AppointmentList.get(position);
                AppointmentList.add(0,tempappointment);
                AppointmentList.remove(position+1);
                appointmentAdapter.notifyDataSetChanged();
                alarmDialog(starter.getStringExtra("apttype"),starter.getStringExtra("apttime"));


            } else {
                //Do nothing
            }
        }



    }
    int selectedInd=0;
    public void startRemover(){
        List<String> TitleList = new ArrayList<String>();
        String element;


        if(AppointmentList.size()==0){
            TitleList.add("Empty appointment list");
        }
        else{
            for (int ind =0; ind < AppointmentList.size();ind++){
                Appointment iterator = AppointmentList.get(ind);
                element = ind+""+"         "+iterator.getDate();
                TitleList.add(element);
            }


        }



        final String[] items=TitleList.toArray(new String[TitleList.size()]);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Select an Appointment to Cancel");
        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedInd=  i;
                    }
                });

        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    // mirror image
                    if(items[0].equals("Empty appointment list")  ){


                    }
                    else{
                       // Log.d("aqaqaq",i+"");
                        Appointment InstToDel = AppointmentList.get(selectedInd);
                        //remote remove
                       remoteDelAppointment(InstToDel);

                        //locally remove
                        AppointmentList.remove(selectedInd);
                        appointmentAdapter.notifyDataSetChanged();



                    }
                mRemoveAppointmentDialog.dismiss();
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mRemoveAppointmentDialog.dismiss();
            }
        });
        mRemoveAppointmentDialog = alertBuilder.create();
        mRemoveAppointmentDialog.show();

    }
    public void remoteDelAppointment(Appointment instance){
        FireBaseInfo mFireBaseInfo = new FireBaseInfo();
        SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        Date d_instance_date = new Date();
        int i_initdate = 0;
        try{
            d_instance_date = mdateformat.parse(instance.getDate());
            i_initdate = (int)(d_instance_date.getTime()/1000);
        }catch (ParseException e){
            e.printStackTrace();

        }


        mFireBaseInfo.mFirestore.collection("AAppointment").whereEqualTo("patientid",instance.getPatientID()).whereEqualTo("date",i_initdate).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            QuerySnapshot query_res =  task.getResult();
                            List<DocumentSnapshot> documents = query_res.getDocuments();

                            DocumentSnapshot delDocument = documents.get(0);
                            if(delDocument!=null ){
                                DocumentReference delDocumentRef = delDocument.getReference();
                                delDocumentRef.update("status","Canceled");

                            }else{

                            }


                        } else {

                        }
                    }
                });
    }
    public void initListener(){
        AppointmentRemover = findViewById(R.id.btnDelAppointment);
        AppointmentAdder = findViewById(R.id.btnAddAppointment);;
        AppointmentRemover.setVisibility(View.INVISIBLE);
        AppointmentRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*startRemover();*/
            }
        });

        AppointmentAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_adder();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointmnet_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_appointmnet);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        appointmentAdapter = new AppointmentAdapter(AppointmentList);
        recyclerView.setAdapter(appointmentAdapter );
        mFireBaseInfo = new FireBaseInfo();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
       // setSupportActionBar(toolbar);
        download_medication_list();
        initListener();

        //-----------------------------------------------------

        //--------------------------------------------------
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start_adder();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
            }
        });
    }

}
