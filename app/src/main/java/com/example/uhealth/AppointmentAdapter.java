package com.example.uhealth;
import androidx.appcompat.app.AlertDialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.Activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> implements NotifyInterface {
    private List<Appointment> mAppointmentList;
    private FireBaseInfo mFireBaseInfo;
    private AlertDialog mShowAppointmentDialog;
    private AlertDialog mRemoveAppointmentDialog;
    private AlertDialog mUpdateAppointmentDialog;
    private AlertDialog mFurtherUpdateDialog;
    private int flashsign = 65535;
    @Override
    public void flasher(int position){

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_unit,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }
    public AppointmentAdapter(List<Appointment> appointmentList){
        mAppointmentList=appointmentList;
        mFireBaseInfo= new FireBaseInfo();
    }
    private List<String> showAppointment(Appointment mAppointment){
        List<String> InfoList =new ArrayList<String>();
        InfoList.add(0, "Time:      "+mAppointment.getDate());
        InfoList.add(1,"Type:       "+ mAppointment.getAppointmentType());
        InfoList.add(2, "Location:      "+mAppointment.getApptLocation());

        InfoList.add(3, "Physician:       "+mAppointment.getPhysicianName());
        return InfoList;

    }
    public void startUpdaterActivity(View v,Appointment instance){
        Intent FurtherUpdate = new Intent(v.getContext(),AppointmentUpdater.class);
        FurtherUpdate.putExtra("apttype",instance.getAppointmentType());
        FurtherUpdate.putExtra("apttime",instance.getDate());
        v.getContext().startActivity(FurtherUpdate);


    }
    private String postponed_date;
    public void updateAppointmentDate(String postponed_date,Appointment mAppointment){
        int i_postponeddate = 0;
        try {
            SimpleDateFormat mdateformat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
            Date date_time = mdateformat.parse(postponed_date);
            ;
             i_postponeddate  =(int)(date_time.getTime()/1000);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        final int f_postponeddate =i_postponeddate;

        mFireBaseInfo.mFirestore.collection("AAppointment").whereEqualTo("date",mAppointment.getintDate())
                .whereEqualTo("patientid", mAppointment.getPatientID()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            QuerySnapshot query_res = task.getResult();
                            List<DocumentSnapshot> documents = query_res.getDocuments();

                            DocumentSnapshot delDocument = documents.get(0);
                            if (delDocument != null) {
                                DocumentReference delDocumentRef = delDocument.getReference();
                                delDocumentRef.update("date", f_postponeddate);
                                delDocumentRef.update("status", "Scheduled");
                                // startUpdaterActivity(vv, mAppointment);
                                mUpdateAppointmentDialog.dismiss();

                            } else {

                            }


                        } else {
                            // Toast.makeText(ProfileActivity.this, "Ouch!!!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
    public void updateAppointmentTime(View v,Appointment mAppointment){
        final View vv = v;
        final Appointment f_appointment =  mAppointment;
        final Calendar calendar = Calendar.getInstance();
        final List<String> selected_time = new ArrayList<String>();
        int[] timelist=new int[5];
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        final String init_time = "1970-01-01-23:59";
        selected_time.add("1970-01-01-23:59");
        //String selected_time="1970-01-01-23:59";


        final TimePickerDialog timePickerDialog = new TimePickerDialog(vv .getContext(), new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timelist[3] = hourOfDay;
                timelist[4] = minute;
                selected_time.remove(selected_time.get(0));
                String timetoAdd = String.format("%04d-%02d-%02d-%02d:%02d",timelist[0],timelist[1],timelist[2],timelist[3],timelist[4]);
                selected_time.add(0,timetoAdd);
                postponed_date = timetoAdd;
                updateAppointmentDate(postponed_date,f_appointment);

            }
        }, hour, minute, true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(vv.getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                timelist[0] = year;
                timelist[1] = monthOfYear + 1;
                timelist[2]= dayOfMonth;
                timePickerDialog.show();
            }
        }, year, month, day);

        datePickerDialog.show();

    }

    public void remoteDelAppointment(Appointment instance,int position){
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
                                mAppointmentList.remove(position);

                            }else{

                            }


                        } else {

                        }
                    }
                });
    }
    public void startRemover(int position,View v){
        List<String> TitleList = new ArrayList<String>();
        String element;

        //final String[] items=TitleList.toArray(new String[TitleList.size()]);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(v.getContext());
        alertBuilder.setTitle("Delete this Appointment?");


        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // mirror image

                // Log.d("aqaqaq",i+"");
                Appointment InstToDel = mAppointmentList.get(position);
                //remote remove
                remoteDelAppointment(InstToDel,position);

                notifyDataSetChanged();




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
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Appointment mAppointment=mAppointmentList.get(position);
        //note from res id to actual bitmap
        //holder.fruitImage.setImageResource(mphoto.getImageID());
        String displayed = position+"  "+mAppointment.getAppointmentType();
        holder.singleAppointmentRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRemover(position,v);
            }
        });
        holder.appointmentTag.setText(displayed);
        String appstatus = mAppointment.getStatus();
        Date curDate = new Date();
        switch(appstatus){
            case "Finished":{

                holder.appointmentCardView.setCardBackgroundColor(ContextCompat.getColor( holder.appointmentCardView.getContext(), R.color.lightviolet));
                break;
            }
            case "Scheduled":{
                if(curDate.getTime()-(long)1000*mAppointment.getintDate() > 0){
                    holder.appointmentUpdater.setVisibility(View.VISIBLE);
                }
                holder.appointmentCardView.setCardBackgroundColor(ContextCompat.getColor( holder.appointmentCardView.getContext(), R.color.lightgreen));
                break;
            }
            case "Past":{
                if(curDate.getTime()-(long)1000*mAppointment.getintDate() > 0){
                    holder.appointmentUpdater.setVisibility(View.VISIBLE);
                }
                holder.appointmentCardView.setCardBackgroundColor(ContextCompat.getColor( holder.appointmentCardView.getContext(), R.color.lightyellow));
                break;
            }
            default:{
                break;
            }
        }
        holder.dateView.setText("Date: "+mAppointment.getDate());
        holder.locationView.setText("Location :"+mAppointment.getApptLocation());
        holder.physicianView.setText("Doctor :"+mAppointment.getPhysicianName());
        holder.details.setVisibility(View.GONE);
        holder.appointmentCardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int state =  holder.details.getVisibility();
                if(state==View.GONE){
                    holder.details.setVisibility(View.VISIBLE);
                }else{
                    holder.details.setVisibility(View.GONE);
                }
            }
        });
        holder.appointmentChecker.setVisibility(View.GONE);
        /*holder.appointmentChecker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> SelectedAppointment =  showAppointment(mAppointment);
                final String[] items =  SelectedAppointment.toArray(new String[SelectedAppointment.size()]);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(v.getContext());
                alertBuilder.setTitle("Check Appointment");
                alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                            }
                        });
                alertBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mShowAppointmentDialog.dismiss();
                    }
                });
                mShowAppointmentDialog = alertBuilder.create();
                mShowAppointmentDialog.show();
            }
        });*/
        Date ApptDate = new Date();
        ApptDate.setTime((long)1000*mAppointment.getintDate());
        Date Current = new Date();
        if(Current.getTime()-ApptDate.getTime()>0){
            if(!("Finished".equals(mAppointment.getStatus()))) {


                holder.appointmentUpdater.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mAppointment
                        final String[] items = {"Finished", "Canceled", "Postponed"};

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(v.getContext());
                        alertBuilder.setTitle("Select your appointment feedback.");
                        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mListStack.add(0, items[i]);


                                //Toast.makeText(MainActivity.this, items[i], Toast.LENGTH_SHORT).show();
                            }
                        });


                        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mListStack.add("Finished");
                                switch (mListStack.get(0)) {

                                    case "Finished": {


                                        //
                                        final View vv = v;



                                        String selectedUpdate = mListStack.get(0);
                                        mFireBaseInfo.mFirestore.collection("AAppointment").whereEqualTo("date",mAppointment.getintDate())
                                                .whereEqualTo("patientid", mAppointment.getPatientID()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {

                                                            QuerySnapshot query_res = task.getResult();
                                                            List<DocumentSnapshot> documents = query_res.getDocuments();

                                                            DocumentSnapshot delDocument = documents.get(0);
                                                            if (delDocument != null) {
                                                                DocumentReference delDocumentRef = delDocument.getReference();
                                                                delDocumentRef.update("status", selectedUpdate);
                                                                startUpdaterActivity(vv, mAppointment);
                                                                mUpdateAppointmentDialog.dismiss();

                                                            } else {

                                                            }


                                                        } else {
                                                            // Toast.makeText(ProfileActivity.this, "Ouch!!!", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                        // furtherUpdate(vv,mAppointment);

                                        break;
                                    }
                                    case "Postponed":{
                                        updateAppointmentTime(v, mAppointment);
                                        break;
                                    }
                                    case "Canceled":{
                                        final View vv = v;



                                        String selectedUpdate = mListStack.get(0);
                                        mFireBaseInfo.mFirestore.collection("AAppointment").whereEqualTo("date",mAppointment.getintDate())
                                                .whereEqualTo("patientid", mAppointment.getPatientID()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {

                                                            QuerySnapshot query_res = task.getResult();
                                                            List<DocumentSnapshot> documents = query_res.getDocuments();

                                                            DocumentSnapshot delDocument = documents.get(0);
                                                            if (delDocument != null) {
                                                                DocumentReference delDocumentRef = delDocument.getReference();
                                                                delDocumentRef.update("status", selectedUpdate);
                                                               // startUpdaterActivity(vv, mAppointment);
                                                                mUpdateAppointmentDialog.dismiss();

                                                            } else {

                                                            }


                                                        } else {
                                                            // Toast.makeText(ProfileActivity.this, "Ouch!!!", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                        break;
                                    }
                                    default:{
                                        break;
                                    }

                                }
                                //end case
                            }

                        });


                        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mUpdateAppointmentDialog.dismiss();
                            }
                        });
                        mUpdateAppointmentDialog = alertBuilder.create();

                        try {
                            SimpleDateFormat mdateformat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                            Date date_time = mdateformat.parse(mAppointment.getDate());
                            ;
                            final Date currentDate = new Date();

                            if (currentDate.getTime() - date_time.getTime() > 0) {
                                mUpdateAppointmentDialog.show();
                            } else {
                                // nothing happened
                            }
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }else{
                holder.appointmentUpdater.setVisibility(View.INVISIBLE);
            }
        }else{
            holder.appointmentUpdater.setVisibility(View.INVISIBLE);
        }

    }
    private List<String> mListStack = new ArrayList<String>();





    @Override
    public int getItemCount() {
        return mAppointmentList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView appointmentTag;
       // Button appointmentRemover;
        CardView appointmentCardView;
        Button appointmentChecker;
        Button appointmentUpdater;
        LinearLayout details;
        TextView physicianView ;
        TextView locationView;
        TextView dateView;
        Button singleAppointmentRemover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);//btnAppointmentUpdater
            details = itemView.findViewById(R.id.appointment_detail_group);
            physicianView = itemView.findViewById(R.id.physicianview);
            locationView = itemView.findViewById(R.id.locationview);
            dateView = itemView.findViewById(R.id.dateview);
            appointmentCardView = itemView.findViewById(R.id.appointment_card_view);
            appointmentTag =itemView.findViewById(R.id.appointmnet_text_unit);
            //appointmentRemover =itemView.findViewById(R.id.btnAppointmentRemover);
            appointmentChecker =itemView.findViewById(R.id.btnAppointmentChecker);
            appointmentUpdater =itemView.findViewById(R.id.btnAppointmentUpdater);
            singleAppointmentRemover = itemView.findViewById(R.id.btnSingleAppointmentRemover);

        }
    }
    //


}
