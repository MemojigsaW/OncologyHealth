package com.example.uhealth;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AppointmentAdder extends AppCompatActivity {

    private Button m_adder ;// findViewById(R.id.btnAddAppointmnet);
    private Button time_option ;// findViewById(R.id.btnPickTime);

    TextView timeboard ;// findViewById(R.id.selectedTime);
    Button btnSelectType;
    TextView selectedtype;
    EditText doctornameview ;// findViewById(R.id.appointment_physcian_name);
    EditText locationview ;//findViewById(R.id.appointment_location);

    private AlertDialog mtypeSelectionDialog;
    private FireBaseInfo mFireBaseInfo;
    String EndTime = "1970-01-01-23:59";
    final static int SET_APPOINTMENT_ALARM = 15;


    public void appointmentListener(){
       final Date currentDate = new Date();
       SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
       String appointment_time = timeboard.getText().toString();
       //String current_time_text = mdateformat.format(currentDate);
       String physician_name = doctornameview.getText().toString();
       String str_selected_type = selectedtype.getText().toString();
       String mLocation =  locationview.getText().toString();
       String username = "Default Name";
       String userid = mFireBaseInfo.mAuth.getUid();
       // String userid = "uid123";
        Date date_time = new Date();
        String status ="Scheduled";

        HashMap<String,Object> resMap = new HashMap<String,Object>();
        try{
            Long comp = mdateformat.parse(appointment_time).getTime() - currentDate.getTime();

            if(comp > 0){
                if(comp > 1000*60*60*24){ //longer than 1 day
                    resMap.put("status","Scheduled");
                    status = "Scheduled";
                }
                else{
                    resMap.put("status","Scheduled");
                    status = "Scheduled";
                }
            }
            else{
                resMap.put("status","Past");
                status = "Past";
            }


        }catch(ParseException e){
            e.printStackTrace();
        }
        resMap.put("username",username);
        resMap.put("uid",userid);
        resMap.put("status",status);
        resMap.put("type",str_selected_type);
        resMap.put("date",appointment_time);
        resMap.put("physicainname",physician_name);
        resMap.put("patientid",userid);
        resMap.put("patientname",username);
        resMap.put("location",mLocation );


       Intent intent = new Intent();

       //intent.putExtra("data_return", resMap);

        intent.putExtra("username",username);
        intent.putExtra("uid",userid);
        intent.putExtra("status",status);
        intent.putExtra("type",str_selected_type);
        intent.putExtra("date",appointment_time);
        intent.putExtra("physicainname",physician_name);
        intent.putExtra("patientid",userid);
        intent.putExtra("patientname",username);
        intent.putExtra("location",mLocation );

        Log.d("HHHH","we are 0002");
       setResult(RESULT_OK, intent);

       finish();


       // SimpleDateFormat mdateformat=getDateTimeInstance("yyyy-MM-dd-HH:mm")
        //"1970-01-01-23:59";
    }

    public void endTimePicker(){

        final Calendar calendar = Calendar.getInstance();
        final List<String> selected_time = new ArrayList<String>();
        final int[] timelist=new int[5];
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        final String init_time = "1970-01-01-23:59";
        selected_time.add("1970-01-01-23:59");
        //String selected_time="1970-01-01-23:59";


        final TimePickerDialog timePickerDialog = new TimePickerDialog(AppointmentAdder.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timelist[3] = hourOfDay;
                timelist[4] = minute;
                selected_time.remove(selected_time.get(0));
                String timetoAdd = String.format("%04d-%02d-%02d-%02d:%02d",timelist[0],timelist[1],timelist[2],timelist[3],timelist[4]);
                selected_time.add(0,timetoAdd);
                //timeboard.setText(timetoAdd);
                EndTime = timetoAdd;

            }
        }, hour, minute, true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AppointmentAdder.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                timelist[0] = year;
                timelist[1] = monthOfYear+1;
                timelist[2]= dayOfMonth;
                timePickerDialog.show();
            }
        }, year, month, day);

        datePickerDialog.show();

    }
    public void startTimePicker(){
        final Calendar calendar = Calendar.getInstance();
        final List<String> selected_time = new ArrayList<String>();
        final int[] timelist=new int[5];
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        final String init_time = "1970-01-01-23:59";
        selected_time.add("1970-01-01-23:59");
        //String selected_time="1970-01-01-23:59";


        final TimePickerDialog timePickerDialog = new TimePickerDialog(AppointmentAdder.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timelist[3] = hourOfDay;
                timelist[4] = minute;
                selected_time.remove(selected_time.get(0));
                String timetoAdd = String.format("%04d-%02d-%02d-%02d:%02d",timelist[0],timelist[1],timelist[2],timelist[3],timelist[4]);
                selected_time.add(0,timetoAdd);
                timeboard.setText(timetoAdd);

            }
        }, hour, minute, true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AppointmentAdder.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                timelist[0] = year;
                timelist[1] = monthOfYear+1;
                timelist[2]= dayOfMonth;
                timePickerDialog.show();
            }
        }, year, month, day);

        datePickerDialog.show();

    }
    public void typeSelectionDialogue(){
        final String[] items = {"office appointment","chemotherapy","surgery or procedure appointment","radiation session","imaging appointment"};

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Select your appointment type.");
        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedtype.setText(items[i]);
                //Toast.makeText(MainActivity.this, items[i], Toast.LENGTH_SHORT).show();
            }
        });


        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mtypeSelectionDialog.dismiss();
            }
        });


        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mtypeSelectionDialog.dismiss();
            }
        });
        mtypeSelectionDialog = alertBuilder.create();
        mtypeSelectionDialog.show();




    }
    public void initListener(){
        m_adder =findViewById(R.id.btnAddAppointmnet);
        time_option =findViewById(R.id.btnPickTime);
        timeboard = findViewById(R.id.selectedTime);
        doctornameview = findViewById(R.id.appointment_physcian_name);
        locationview = findViewById(R.id.appointment_location);

        btnSelectType = findViewById(R.id.btnPickType);
        selectedtype = findViewById(R.id.selectedType);




        timeboard.setText("1970-01-01-23:59");
        btnSelectType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeSelectionDialogue();

            }
        });
        time_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startTimePicker();
            }
        });
        m_adder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                appointmentListener();
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_adder);
        mFireBaseInfo =new FireBaseInfo();
        initListener();

    }
    //Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar
        //setSupportActionBar(toolbar);



        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
            }
        });*/


    }


