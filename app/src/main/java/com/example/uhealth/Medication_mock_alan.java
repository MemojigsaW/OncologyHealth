package com.example.uhealth;

import java.util.HashMap;

public class Medication_mock_alan {
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
        private String LastUpdate;//time
        private String NextUpdate;//to be updated

        //--------Initializer------------
        public Medication_mock_alan(HashMap<String,Object> Initializer){
            this.Type  = Initializer.get("type").toString();
            this.CurrentStorage = Integer.valueOf(Initializer.get("currentstorage").toString());


           this.Status  =Initializer.get("status").toString();
            this.Dosis = Long.valueOf(Initializer.get("dosis").toString());
;            this.Medicine = Initializer.get("medicine").toString();
            this.InitDate = ((Long)Initializer.get("initdate")).intValue();
            this.InitStorage =  Long.valueOf(Initializer.get("initstorage").toString());
            this.Interval =  Long.valueOf(Initializer.get("interval").toString());
           // this.Repeats = Long.valueOf(Initializer.get("repeats").toString());
            this.LastUpdate = Initializer.get("lastupdate").toString();
            this.NextUpdate = Initializer.get("nextupdate").toString();
            this.Username =Initializer.get("username").toString();
            this.Uid = Initializer.get("uid").toString();
            // no user id yet
            //this.CurrentStorage = (int)Initializer.get("currentstorage");
            if(this.getStatus().equals("complete")){
                this.EndDate = ((Long)Initializer.get("enddate")).intValue();
            }else{
                this.EndDate = 0;
            }

        }
    //----Get-------------
        public String getType(){return Type;}
        public int getEndDate(){return EndDate;}
        public int getCurrentStorage(){return CurrentStorage;}
        public String getStatus(){return Status;}
      //  public long getRepeats(){return Repeats;}
        public String getUsername(){return Username;}
        public String getUid(){return Uid;}
        public long getDosis(){return Dosis;}
        public String getMedicine(){return Medicine;}
        public int getInitDate(){return InitDate; }
        public long getInitStorage(){return InitStorage;}
        public long getInterval(){return Interval;}
        public String getLastUpdate(){return LastUpdate;}
        public String getNextUpdate(){return NextUpdate;}
       // public int getCurrentStorage(){return CurrentStorage;}
        //--Set-----------
        public void setType(String Type){this.Type = Type;}
        public void setEndDate(int EndDate){this.EndDate = EndDate;}
        public void setCurrentStorage(int CurrentStorage){this.CurrentStorage = CurrentStorage;}

        public void setStatus(String Status){this.Status = Status;}
        public void setLastUpdate(String LastUpdate){this.LastUpdate = LastUpdate;}
    public void setNextUpdate(String NextUpdate){this.NextUpdate = NextUpdate;}
        public void setMedicine(String Medicine){this.Medicine = Medicine;}
        public void setInitDate(int InitDate){this.InitDate = InitDate;}
        public void setInitStorage(long InitStorage){this.InitStorage = InitStorage;}
        public void setInterval(long Interval){this.Interval = Interval;}
        //public void setRepeats(long Repeats){this.Repeats = Repeats;}
        public void setDosis(long Dosis){this.Dosis = Dosis;}
        public void setUid(String uid){this.Uid = Uid;};
        public void setUsername(String Username){this.Username = Username;}
        //public void setCurrentStorage(int CurrentStorage){this.CurrentStorage = CurrentStorage;}




}
