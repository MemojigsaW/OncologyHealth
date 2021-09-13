package com.example.uhealth.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.Activity.PastDiagnosis;
import com.example.uhealth.R;

public class rvadapter_diag extends RecyclerView.Adapter<rvadapter_diag.DropDownDiagVH> {
    private Context mContext;
    private String[] categorylist;

    public rvadapter_diag(Context i_context, String[] input){
        mContext = i_context;
        categorylist = input;
    }


    @NonNull
    @Override
    public DropDownDiagVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_diag, parent, false);
        DropDownDiagVH vh = new DropDownDiagVH(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DropDownDiagVH holder, int position) {
        String title = categorylist[position];
        holder.title.setText(title);
//        check if in open position
        if(((PastDiagnosis)(mContext)).containsPosition(position)){
            holder.expandable.setVisibility(View.VISIBLE);
            holder.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
        }else{
            holder.expandable.setVisibility(View.GONE);
            holder.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
        }
//        switch to set nestedRV and editText
        NestedRVAdp_diag mAdapter = null;
        switch (title){
            case "Neurological Diseases":
                String[] diseaselist = mContext.getResources().getStringArray(R.array.Neurological_Disease);
                mAdapter = new NestedRVAdp_diag(mContext, diseaselist);
                break;
            case "Sensory Diseases":
                String[] diseaselist2 = mContext.getResources().getStringArray(R.array.Sensory_Disease);
                mAdapter = new NestedRVAdp_diag(mContext, diseaselist2);
                break;
            case "Endocrine Diseases":
                String[] diseaselist3 = mContext.getResources().getStringArray(R.array.Endocrine_Disease);
                mAdapter = new NestedRVAdp_diag(mContext, diseaselist3);
                break;
            case "Cardiovascular Diseases":
                String[] diseaselist4 = mContext.getResources().getStringArray(R.array.Cardiovascular_Disease);
                mAdapter = new NestedRVAdp_diag(mContext, diseaselist4);
                break;
            case "Lung Diseases":
                String[] diseaselist5 = mContext.getResources().getStringArray(R.array.Lung_Disease);
                mAdapter = new NestedRVAdp_diag(mContext, diseaselist5);
                break;
            case "Gastrointestinal Diseases":
                String[] diseaselist6 = mContext.getResources().getStringArray(R.array.Gastrointestinal_Disease);
                mAdapter = new NestedRVAdp_diag(mContext, diseaselist6);
                break;
            case "Genitourinary Disorders":
                String[] diseaselist7 = mContext.getResources().getStringArray(R.array.Genitourinary_Disorders);
                mAdapter = new NestedRVAdp_diag(mContext, diseaselist7);
                break;
            case "Vascular Diseases":
                String[] diseaselist8 = mContext.getResources().getStringArray(R.array.Vascular_Disease);
                mAdapter = new NestedRVAdp_diag(mContext, diseaselist8);
                break;
            case "Rheumatoid Diseases":
                String[] diseaselist9 = mContext.getResources().getStringArray(R.array.Rheumatoid_Disease);
                mAdapter = new NestedRVAdp_diag(mContext, diseaselist9);
                break;
            case "Cancers":
                String[] diseaselist10 = mContext.getResources().getStringArray(R.array.Cancer);
                mAdapter = new NestedRVAdp_diag(mContext, diseaselist10);
                break;
            case "Miscellaneous":
                String[] diseaselist11 = mContext.getResources().getStringArray(R.array.Miscellaneous);
                mAdapter = new NestedRVAdp_diag(mContext, diseaselist11);
                break;
            default:
                Log.d(PastDiagnosis.TAG, "nested RV switch statement failed? should never happen");
                break;
        }
        RecyclerView.LayoutManager mlayoutManager = new GridLayoutManager(mContext, 1);
        holder.nestedRV.setAdapter(mAdapter);
        holder.nestedRV.setLayoutManager(mlayoutManager);
        holder.nestedRV.setHasFixedSize(true);

        String full = categorylist[position];
        String[] fullparse = full.split(" ");
        String key = fullparse[0];
        String ET = ((PastDiagnosis)(mContext)).getOthers(key);
        holder.otherbox.setText(ET);
    }

    @Override
    public int getItemCount() {
        if (categorylist!=null){
            return categorylist.length;
        }else{
            return 0;
        }
    }

    public class DropDownDiagVH extends RecyclerView.ViewHolder {
        TextView title;
        ImageView arrow;
        RelativeLayout expandable, titlelayout;
        RecyclerView nestedRV;
        EditText otherbox;

        public DropDownDiagVH(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_main);
            arrow = itemView.findViewById(R.id.title_arrow);
            expandable = itemView.findViewById(R.id.expandable_content);
            nestedRV = itemView.findViewById(R.id.nestedrv_diag);
            otherbox = itemView.findViewById(R.id.diag_other);
            titlelayout = itemView.findViewById(R.id.expandable_title);


            titlelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(((PastDiagnosis)(mContext)).containsPosition(position)){
                        ((PastDiagnosis)(mContext)).rmPosition(position);
                    }else{
                        ((PastDiagnosis)(mContext)).addPosition(position);
                    }
                    notifyItemChanged(position);
                }
            });

            otherbox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String full = categorylist[getAdapterPosition()];
                    String[] fullparse = full.split(" ");
                    String key = fullparse[0];
                    ((PastDiagnosis)(mContext)).putOthers(key, s.toString());
                }
            });
        }
    }

}
