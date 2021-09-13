package com.example.uhealth.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.Fragments.TimelineFilter;
import com.example.uhealth.R;

import org.w3c.dom.Text;

public class rvadapter_tlfiltertype extends RecyclerView.Adapter<rvadapter_tlfiltertype.Filtertype> {
    String[] itemarray;
    TimelineFilter.filtercallback filtercallback;

//    constructor
    public rvadapter_tlfiltertype(String[] input, TimelineFilter.filtercallback callback){
        this.itemarray = input;
        filtercallback = callback;
    }

    @NonNull
    @Override
    public Filtertype onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_timeline_filtertype_item, parent, false);
        Filtertype filtertype = new Filtertype(view);
        return filtertype;
    }

    @Override
    public void onBindViewHolder(@NonNull Filtertype holder, int position) {
        holder.filtertext.setText(itemarray[position]);
        String value = itemarray[position];
        if (filtercallback.checkoption(value)){
            holder.filtercheck.setChecked(true);
        }else{
            holder.filtercheck.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return itemarray.length;
    }

    public class Filtertype extends RecyclerView.ViewHolder {
        CheckBox filtercheck;
        TextView filtertext;

        public Filtertype(@NonNull View itemView) {
            super(itemView);
            filtercheck = itemView.findViewById(R.id.timelinefiltertype_check);
            filtertext = itemView.findViewById(R.id.timelinefiltertype_text);

            filtercheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos = getAdapterPosition();
                    String value = itemarray[pos];
                    if (isChecked){
                        if (!filtercallback.checkoption(value)){
                            filtercallback.addoption(value);
                        }
                    }else{
                        if (filtercallback.checkoption(value)){
                            filtercallback.rmoption(value);
                        }
                    }
                }
            });
        }
    }
}
