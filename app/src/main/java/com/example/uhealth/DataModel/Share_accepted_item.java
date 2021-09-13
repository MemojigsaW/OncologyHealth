package com.example.uhealth.DataModel;

import java.util.HashMap;

public class Share_accepted_item extends Share_outstandings_item{
    private boolean mInfoandHistory, mDiagnosis, mTimeline;

    public Share_accepted_item(HashMap<String, Object> in_map) {
        super(in_map);
    }


    public boolean ismInfoandHistory() {
        return mInfoandHistory;
    }

    public void setmInfoandHistory(boolean mInfoandHistory) {
        this.mInfoandHistory = mInfoandHistory;
    }

    public boolean ismDiagnosis() {
        return mDiagnosis;
    }

    public void setmDiagnosis(boolean mDiagnosis) {
        this.mDiagnosis = mDiagnosis;
    }

    public boolean ismTimeline() {
        return mTimeline;
    }

    public void setmTimeline(boolean mTimeline) {
        this.mTimeline = mTimeline;
    }
}
