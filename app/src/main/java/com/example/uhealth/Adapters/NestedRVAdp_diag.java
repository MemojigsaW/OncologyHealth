package com.example.uhealth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.Activity.PastDiagnosis;
import com.example.uhealth.R;

public class NestedRVAdp_diag extends RecyclerView.Adapter<NestedRVAdp_diag.nestedDiag_VH> {
    private String[] diseaselist;
    private Context mContext;

    public NestedRVAdp_diag(Context i_context, String[] i_list){
        mContext = i_context;
        diseaselist = i_list;
    }


    @NonNull
    @Override
    public nestedDiag_VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nestedrv_diag, parent, false);
        nestedDiag_VH vh = new nestedDiag_VH(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull nestedDiag_VH holder, int position) {
        String diease = diseaselist[position];
        holder.checkBox.setText(diease);
        if (((PastDiagnosis)(mContext)).containsDisease(diease)){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        if (diseaselist!=null){
            return diseaselist.length;
        }else{
            return 0;
        }
    }

    public class nestedDiag_VH extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        public nestedDiag_VH(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.nested_diag_checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos = getAdapterPosition();
                    String disease = diseaselist[pos];
                    if (isChecked){
                        ((PastDiagnosis)(mContext)).addDisease(disease);
                    }else{
                        ((PastDiagnosis)(mContext)).rmDisease(disease);
                    }
                }
            });
        }
    }
}
