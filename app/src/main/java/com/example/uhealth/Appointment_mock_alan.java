package com.example.uhealth;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;

public class Appointment_mock_alan {
    //Status managed as int singals: PAST NEAR.TODAY
    private String Status;
    //private String PhysicianID;
    private String AppointmentType;
    private String PhysicianName;
    private String PatientID;
    private String PatientName;
    private String ApptLocation;

    private int ApptDate;
    private String Note;
    private List<String> ImagePaths;

    //-----------------------
    public Appointment_mock_alan(HashMap<String,Object> Initializer){
        this.Status = Initializer.get("status").toString();
        this.AppointmentType = Initializer.get("type").toString();
        this.ApptDate = ((Long)Initializer.get("date")).intValue();
        //this.PhysicianID = Initializer.get("physicianid");
        this.PhysicianName = Initializer.get("physicainname").toString();
        this.PatientID = Initializer.get("patientid").toString();
        this.PatientName = Initializer.get("patientname").toString();
        this.ApptLocation = Initializer.get("location").toString();
        this.Note = Initializer.get("note").toString();
        this.ImagePaths = (List<String>)Initializer.get("path");
    }
    //----------Get-------------
    public String getStatus(){
       return Status;
    }
    public String getAppointmentType(){return AppointmentType;}
    public int getDate(){
        return ApptDate;
    }
    //public String getPhysicianID(){ return PhysicianID; }
    public String getPatientID(){ return PatientID; }
    public String getPatientName(){return PatientName; }
    public String getPhysicianName(){return PhysicianName;}
    public String getApptLocation(){return ApptLocation;}
    //------------Set---------------
    public void setStatus(String Status){this.Status = Status;}
    public void setAppointmentType(String appointmentType){this.AppointmentType = appointmentType;}
    public void setDate(int ApptDate){this.ApptDate = ApptDate;}
    public void setPatientID(String PatientID){this.PatientID = PatientID;}
    public void setPatientName(String PatientName){this.PatientName = PatientName;}
    //public void setPhysicianID(String PhysicianID){this.PhysicianID = PhysicianID;}
    public void setPhysicianName(String PhysicianName){this.PhysicianName= PhysicianName;}
    public void setApptLocation(String Location){this.ApptLocation = Location;}

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public List<String> getImagePaths() {
        return ImagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        ImagePaths = imagePaths;
    }
}
