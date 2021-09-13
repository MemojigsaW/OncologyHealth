package com.example.uhealth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MedicationList extends AppCompatActivity {
    final static  List<Medication> MedicationList = new ArrayList<>();
    List<Medication> UpdateMedicationList=new ArrayList<>();
    List<Medication> YetUpdateMedicationList=new ArrayList<>();
   MedicationAdapter medicationAdapter;
    private FireBaseInfo mFireBaseInfo;
    Button MedicationRemover,MedicationAdder;
    private AlertDialog mRemoveMedicationDialog;
    private AlertDialog mAlarmMedicationDialog;
    final static int REQUEST_ADD_A_MEDICATION = 200;
    private MediaPlayer mp = new MediaPlayer();
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
    public void tuning(){
        if(mp.isPlaying()){
            mp.pause();
        }else{
            //mp.start();
        }

    }
    public void start_adder(){
        Intent addMedIntent = new Intent(this,MedicationAdder.class);
        startActivityForResult(addMedIntent, REQUEST_ADD_A_MEDICATION);

    }
    public void finalDemo(Date date_time,String medicine,int position){
        SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        long time = System.currentTimeMillis();

        Intent intent = new Intent(MedicationList.this,MyAlarmReceiver.class);
        intent.putExtra("isMedication",true);
        intent.putExtra("medtime",mdateformat.format(date_time));
        intent.putExtra("medicine",medicine);
        intent.putExtra("position",position);
        PendingIntent pi = PendingIntent.getBroadcast(MedicationList.this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //manager.set(AlarmManager.RTC_WAKEUP,date_time.getTime(),pi);
        manager.set(AlarmManager.RTC_WAKEUP,date_time.getTime() ,pi);

    }
    public void booleanArrange(Medication newinstance){
        boolean sign = false;
        Date cur = new Date();
        if("periodical".equals(newinstance.getType())&&"ongoing".equals(newinstance.getStatus())){
            if(cur.getTime()/1000 > newinstance.getintNextUpdate()){
                sign = true;
                bubble(newinstance);
            }
        }
        if(sign == false){
            bubble(newinstance);
        }
        else{
            MedicationList.add(0,newinstance);
        }
    }
    public int bubble(Medication newinstance){
        final Date currentDate = new Date();
        SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        String init_time = newinstance.getInitDate();
        Long newtime =new  Long(0);
        Long oldtime =new Long(0);
        int index =0;
        final int count = MedicationList.size();
        if(count<=0){
            MedicationList.add(newinstance);
        }
        else{
            for(int i =0; i <=count;i++){
                if(i==count)
                {
                    MedicationList.add(i,newinstance);
                    index = i;
                }else{
                    int res = 0;
                    try{
                        newtime = mdateformat.parse(init_time ).getTime();
                    }
                    catch(Exception e){

                    }
                    try{
                        oldtime = mdateformat.parse(MedicationList.get(i).getInitDate()).getTime();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if(newtime<oldtime){

                    }
                    else{
                        MedicationList.add(i,newinstance);
                        index = i;
                        break;
                    }
                }


            }

        }
        return index;
    }
    public void download_medication_list(){
        String uid = mFireBaseInfo.mUser.getUid();
        mFireBaseInfo.mFirestore.collection("MMedication").whereEqualTo("uid",uid).get()
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
                    //int to string

                    Date t_date = new Date();
                    try{
                        t_date.setTime(1000*Long.valueOf(resMap.get("initdate").toString()));
                        resMap.put("initdate",mdateformat.format(t_date));
                        t_date.setTime(1000*Long.valueOf(resMap.get("enddate").toString()));
                        resMap.put("enddate",mdateformat.format(t_date));
                        t_date.setTime(1000*Long.valueOf(resMap.get("lastupdate").toString()));
                        resMap.put("lastupdate",mdateformat.format(t_date));
                        t_date.setTime(1000*Long.valueOf(resMap.get("nextupdate").toString()));
                        resMap.put("nextupdate",mdateformat.format(t_date));
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }


                    //end int to string
                    Medication mmedication = new Medication(resMap);
                    if("canceled".equals(mmedication.getStatus())){

                    }else{

                       bubble(mmedication );
                    }

                }
                MedicationList.addAll(UpdateMedicationList);
                MedicationList.addAll(YetUpdateMedicationList);
                medicationAdapter.notifyDataSetChanged();



            } else {

            }
        }
    });



    }
    public void upload_medication_instance(HashMap<String,Object> resMap){

        SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");


        try{
            final Date t_date = new Date();
            final String old_initdate = resMap.get("initdate").toString();
            t_date.setTime(mdateformat.parse(old_initdate ).getTime());
            final int int_initdate = (int)(t_date.getTime()/1000);
            resMap.put("initdate",int_initdate);

            final String old_enddate = resMap.get("enddate").toString();
            t_date.setTime(mdateformat.parse(old_enddate).getTime());
            final int int_enddate = (int)(t_date.getTime()/1000);
            resMap.put("enddate",int_enddate);

            final String old_lastupdate = resMap.get("lastupdate").toString();
            t_date.setTime(mdateformat.parse(old_lastupdate ).getTime());
            final int int_lastupdate= (int)(t_date.getTime()/1000);
            resMap.put("lastupdate",int_lastupdate);

            final String old_nextupdate = resMap.get("nextupdate").toString();
            t_date.setTime(mdateformat.parse(old_nextupdate ).getTime());
            final int int_nextupdate= (int)(t_date.getTime()/1000);
            resMap.put("nextupdate",int_nextupdate);

            mFireBaseInfo.mFirestore.collection("MMedication").add(resMap) .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                        }

                        ;
                    });
            resMap.put("initdate",old_initdate);
            resMap.put("enddate",old_enddate);
            resMap.put("lastupdate",old_lastupdate);
            resMap.put("nextupdate",old_nextupdate);

        }catch(ParseException e){
            e.printStackTrace();
        }





    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        int istorage=20;
        int icstorage = 20;
        int iinterval = 12;
        int irepeatcounts = 3;
        int dosis = 1;
        switch (requestCode) {

            case REQUEST_ADD_A_MEDICATION  :{
                if (resultCode == RESULT_OK) {

                    HashMap<String,Object> resMap = new HashMap<String,Object>();
                    resMap.put("username",data.getStringExtra("username"));
                    resMap.put("uid",data.getStringExtra("uid"));
                    resMap.put("medicine",data.getStringExtra("medicine"));
                    resMap.put("initdate",data.getStringExtra("initdate"));
                    //---
                    resMap.put("enddate",data.getStringExtra("enddate"));
                    resMap.put("type",data.getStringExtra("type"));


                    //---
                    resMap.put("status",data.getStringExtra("status"));
                    resMap.put("lastupdate",data.getStringExtra("lastupdate"));
                    resMap.put("nextupdate",data.getStringExtra("nextupdate"));
                    try{
                        istorage=Integer.parseInt(data.getStringExtra("initstorage"));

                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    try{
                        icstorage=Integer.parseInt(data.getStringExtra("currentstorage"));

                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    try{
                        iinterval=Integer.parseInt(data.getStringExtra("interval"));

                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }

                    try{
                        dosis =Integer.parseInt(data.getStringExtra("dosis"));

                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    resMap.put("dosis",dosis);
                    resMap.put("initstorage",istorage);
                    resMap.put("currentstorage",icstorage);
                    resMap.put("interval",iinterval);

                    int position = 0;
                    upload_medication_instance(resMap);
                    Medication temp_medication = new Medication(resMap);
                    position=bubble(temp_medication);
                 //   booleanArrange(temp_medication);
                   // MedicationList.clear();
                    //MedicationList.addAll(UpdateMedicationList);
                    //MedicationList.addAll(YetUpdateMedicationList);
                    medicationAdapter.notifyDataSetChanged();
                    try{


                        SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                        Date date_time = mdateformat.parse(resMap.get("nextupdate").toString());;
                        final Date currentDate = new Date();
                        if("ongoing".equals(temp_medication.getStatus())){
                            if( date_time.getTime() -currentDate.getTime()> 0) {



                                //setAlarm(date_time,resMap.get("type").toString());
                                finalDemo(date_time,resMap.get("medicine").toString(), position);
                            }else{

                            }

                        }else{

                        }



                    }catch(java.text.ParseException e){
                        e.printStackTrace();
                    }
                }
            }




            default:{

            }

        }

    }
    int selectedInd=0;
    public void startRemover(){
        List<String> TitleList = new ArrayList<String>();
        String element;


        if(MedicationList.size()==0){
            TitleList.add("Empty medication list");
        }
        else{
            for (int ind =0; ind < MedicationList.size();ind++){
                Medication iterator = MedicationList.get(ind);
                element = ind+""+"Last Update:"+iterator.getLastUpdate();
                TitleList.add(element);
            }


        }



        final String[] items=TitleList.toArray(new String[TitleList.size()]);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Select an Medication to Cancel");
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
                if(items[0].equals("Empty medication list")  ){


                }
                else{
                    // Log.d("aqaqaq",i+"");
                    Medication InstToDel = MedicationList.get(selectedInd);
                    //remote remove
                    remoteDelMedication(InstToDel);

                    //locally remove
                    MedicationList.remove(selectedInd);
                    medicationAdapter.notifyDataSetChanged();



                }
                mRemoveMedicationDialog.dismiss();
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mRemoveMedicationDialog.dismiss();
            }
        });
        mRemoveMedicationDialog = alertBuilder.create();
        mRemoveMedicationDialog.show();

    }
    public void remoteDelMedication(Medication instance){
        FireBaseInfo mFireBaseInfo = new FireBaseInfo();

        SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        Date d_instance_date = new Date();
        int i_initdate = 0;
        try{
            d_instance_date = mdateformat.parse(instance.getInitDate());
            i_initdate = (int)(d_instance_date.getTime()/1000);
        }catch (ParseException e){
            e.printStackTrace();

        }

        mFireBaseInfo.mFirestore.collection("MMedication").whereEqualTo("uid",instance.getUid()).whereEqualTo("initdate",i_initdate).get()

                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            QuerySnapshot query_res =  task.getResult();
                            List<DocumentSnapshot> documents = query_res.getDocuments();

                            DocumentSnapshot delDocument = documents.get(0);
                            if(delDocument!=null ){
                                DocumentReference delDocumentRef = delDocument.getReference();
                                delDocumentRef.update("status","past");

                            }else{

                            }


                        } else {

                        }
                    }
                });
    }
    public void initListener(){
        MedicationRemover = findViewById(R.id.btnDelMedication);
        MedicationAdder = findViewById(R.id.btnAddMedication);;
        MedicationRemover.setVisibility(View.INVISIBLE);
        MedicationRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*startRemover();*/
            }
        });

        MedicationAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_adder();
            }
        });
    }
    public void medicationReminderDialog(String medicine){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        final  String Title = "Medication reminder " ;
        final String[] item ={"Time to take the  "+medicine,};
        alertBuilder.setTitle(Title);
        alertBuilder.setMessage("Time to take the  "+medicine);
        alertBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                tuning();

                mAlarmMedicationDialog.dismiss();
            }
        });
        mAlarmMedicationDialog = alertBuilder.create();
        mAlarmMedicationDialog.show();
    }
    @Override
    protected void onStart(){
        super.onStart();

        Intent starter = getIntent();
      //  download_medication_list();
        int position = 0;
        Bundle extras = starter.getExtras();
        if (extras != null) {
            boolean isMedication = extras.getBoolean("isMedication", false);
            if (isMedication) {
                //-----------------------------------------

                //-----------------------------------------
                startMedia();// Do something
                position = starter.getIntExtra("position",0);
                Medication tempMedication = MedicationList.get(position);
                MedicationList.add(0,tempMedication);
                MedicationList.remove(position+1);
                medicationAdapter.notifyDataSetChanged();
                medicationReminderDialog(starter.getStringExtra("medicine"));

            } else {
                //Do nothing
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);


        mFireBaseInfo =new FireBaseInfo();
        RecyclerView recyclerView = findViewById(R.id.recycler_view_medication);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        medicationAdapter = new MedicationAdapter(MedicationList);
        recyclerView.setAdapter(medicationAdapter );
        download_medication_list();
        medicationAdapter.notifyDataSetChanged();
        initListener();
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_adder();
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
            }
        });
    }

}
