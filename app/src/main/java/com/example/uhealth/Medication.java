package com.example.uhealth;

import android.content.Intent;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Date;

public class Medication {


        private String Status;//Past / Ongoing /
        private String Medicine;
        private int InitDate;
        //--
        private int EndDate;

        private int CurrentStorage;
        private String Type;
        //--
       // private long Repeats;
        private long InitStorage;
        private long Interval;
        private long Dosis;
        private String Username;
        private String Uid;

        private int LastUpdate;//time
        private int NextUpdate;//to be updated
        //resmap reset/ ege
        // --------Initializer------------
        public Medication(HashMap<String,Object> Initializer){
            SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
            this.Type  = Initializer.get("type").toString();
            this.CurrentStorage = Integer.valueOf(Initializer.get("currentstorage").toString());
            try{

                this.EndDate = (int)(mdateformat.parse(Initializer.get("enddate").toString()).getTime()/1000);
                this.InitDate = (int)(mdateformat.parse(Initializer.get("initdate").toString()).getTime()/1000);
                this.LastUpdate =  (int)(mdateformat.parse(Initializer.get("lastupdate").toString()).getTime()/1000);
                this.NextUpdate = (int)(mdateformat.parse(Initializer.get("nextupdate").toString()).getTime()/1000);

            } catch(ParseException e){
                e.printStackTrace();
            }
            //transformation before uploading and after downloading

           this.Status  =Initializer.get("status").toString();
            this.Dosis = Long.valueOf(Initializer.get("dosis").toString());
;            this.Medicine = Initializer.get("medicine").toString();

            this.InitStorage =  Long.valueOf(Initializer.get("initstorage").toString());
            this.Interval =  Long.valueOf(Initializer.get("interval").toString());
           // this.Repeats = Long.valueOf(Initializer.get("repeats").toString());

            this.Username =Initializer.get("username").toString();
            this.Uid = Initializer.get("uid").toString();
            // no user id yet
            //this.CurrentStorage = (int)Initializer.get("currentstorage");
        }
    //----Get-------------
        public String getType(){return Type;}
        public int getintInitDate(){return InitDate;}
        public int getintEndDate(){return  EndDate;
        }
        public int getintLastUpdate(){
            return LastUpdate;
        }
        public int getintNextUpdate(){
            return NextUpdate;
        }
        public String getEndDate(){
            SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
            Date d_EndDate = new Date();
            d_EndDate.setTime((long)EndDate*1000);
            return  mdateformat.format(d_EndDate);
        }
         public String getLastUpdate(){
            SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
             Date d_EndDate = new Date();
             d_EndDate.setTime((long)this.LastUpdate*1000);
             return  mdateformat.format(d_EndDate);}
         public String getNextUpdate(){
             SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
             Date d_EndDate = new Date();
             d_EndDate.setTime((long)this.NextUpdate*1000);
             return  mdateformat.format(d_EndDate);}
        public String getrInitDate(){
            return this.InitDate+"";
        }

        public String getInitDate(){
            SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
            Date d_EndDate = new Date();
            d_EndDate.setTime((long)this.InitDate*1000);
            return  mdateformat.format(d_EndDate);}

        public int getCurrentStorage(){return CurrentStorage;}
        public String getStatus(){return Status;}
      //  public long getRepeats(){return Repeats;}
        public String getUsername(){return Username;}
        public String getUid(){return Uid;}
        public long getDosis(){return Dosis;}
        public String getMedicine(){return Medicine;}

        public long getInitStorage(){return InitStorage;}
        public long getInterval(){return Interval;}

       // public int getCurrentStorage(){return CurrentStorage;}
        //--Set-----------
        public void setType(String Type){this.Type = Type;}

        public void setEndDate(String EndDate){
            SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
            try{
                this.EndDate =(int)(mdateformat.parse(EndDate).getTime()/1000);;
            }catch (ParseException e){
                e.printStackTrace();
            }

        }
    public void setLastUpdate(String LastUpdate){
        SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        try{
            this.LastUpdate =(int)(mdateformat.parse(LastUpdate).getTime()/1000);;
        }catch (ParseException e){
            e.printStackTrace();
        }
        }
    public void setNextUpdate(String NextUpdate) {
        SimpleDateFormat mdateformat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        try {
            this.NextUpdate = (int) (mdateformat.parse(NextUpdate).getTime() / 1000);
            ;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void setInitDate(String InitDate){
            SimpleDateFormat mdateformat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        try {
            this.InitDate= (int) (mdateformat.parse(InitDate).getTime() / 1000);
            ;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        }

        public void setCurrentStorage(int CurrentStorage){this.CurrentStorage = CurrentStorage;}

        public void setStatus(String Status){this.Status = Status;}

        public void setMedicine(String Medicine){this.Medicine = Medicine;}

        public void setInitStorage(long InitStorage){this.InitStorage = InitStorage;}
        public void setInterval(long Interval){this.Interval = Interval;}
        //public void setRepeats(long Repeats){this.Repeats = Repeats;}
        public void setDosis(long Dosis){this.Dosis = Dosis;}
        public void setUid(String uid){this.Uid = Uid;};
        public void setUsername(String Username){this.Username = Username;}
        //public void setCurrentStorage(int CurrentStorage){this.CurrentStorage = CurrentStorage;}




}
