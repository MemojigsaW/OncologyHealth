package com.example.uhealth.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.uhealth.DataModel.timeline_item;
import com.example.uhealth.Interfaces.DataloadedListener;
import com.example.uhealth.repository.timeline_repo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimelineViewModel extends ViewModel {
    private ArrayList<String> typeFilter = new ArrayList<>();
    private DataloadedListener listener;
    private String[] original;
    private int dateFilterInstance = -1;
    private timeline_repo datarepo;
    private MutableLiveData<List<timeline_item>> timelinedata;
    private String uid;

//    Constructor
    public TimelineViewModel(String[] input, String i_uid, DataloadedListener in_listener){
        listener = in_listener;
        original = input;
        typeFilter.clear();
        typeFilter.addAll(Arrays.asList(original));
        uid = i_uid;

        if (timelinedata!=null){
            return;
        }

        in_listener.onBeforeLoaded();
        datarepo = new timeline_repo(listener, uid);
        timelinedata = datarepo.getItems();
    }

    public LiveData<List<timeline_item>> getTl_items(){
        return timelinedata;
    }

//    getter, setters
    public ArrayList<String> getTypeFilter() {
        return typeFilter;
    }
    public String getStringTypeFilter(){
        StringBuilder sb = new StringBuilder();
        for (String s: typeFilter){
            sb.append(s);
        }
        return sb.toString();
    }
    public int getDateFilterInstance() {
        return dateFilterInstance;
    }

    public void add_to_arraylist(String item){
        typeFilter.add(item);
    }
    public void rm_from_arraylist(String item){
        typeFilter.remove(item);
    }
    public boolean checkifin(String item){
        return typeFilter.contains(item);
    }

    public void set_date(int date){
        this.dateFilterInstance = date;
    }
    public int get_date(){
        return dateFilterInstance;
    }

    public void clearall(){
        typeFilter.clear();
        typeFilter.addAll(Arrays.asList(original));
        dateFilterInstance = -1;
    }
}
