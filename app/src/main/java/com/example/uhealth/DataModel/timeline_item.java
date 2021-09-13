package com.example.uhealth.DataModel;

import com.example.uhealth.Appointment_mock_alan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class timeline_item extends Appointment_mock_alan {
    private List<String> mMedls;

    public timeline_item(HashMap<String, Object> Initializer) {
        super(Initializer);
        mMedls = new ArrayList<>();
    }

    public void addMeds(String meds){
        this.mMedls.add(meds);
    }
    public List<String> getmMedls(){return mMedls;}
}

