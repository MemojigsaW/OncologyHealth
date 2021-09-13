package com.example.uhealth.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.R;

import java.util.List;

public class NestedRVAdp_meds extends RecyclerView.Adapter<NestedRVAdp_meds.VH_Nestedrv_meds> {
    private List<String> medlist;

    public NestedRVAdp_meds(List<String> list_meds){
        medlist=list_meds;
    }

    @NonNull
    @Override
    public VH_Nestedrv_meds onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nestedrv_meds, parent, false);
        VH_Nestedrv_meds vh = new VH_Nestedrv_meds(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Nestedrv_meds holder, int position) {
        String med = medlist.get(position);
        holder.medText.setText(med);
    }

    @Override
    public int getItemCount() {
        if (medlist!=null){
            return medlist.size();
        }else{
            return 0;
        }
    }

    public class VH_Nestedrv_meds extends RecyclerView.ViewHolder {
        TextView medText;

        public VH_Nestedrv_meds(@NonNull View itemView) {
            super(itemView);
            medText = itemView.findViewById(R.id.nestedrv_medname);
        }
    }
}
