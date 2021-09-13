package com.example.uhealth.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.Activity.InfoHistoryPage;
import com.example.uhealth.R;

import java.util.ArrayList;

public class rvadapter_initreg_dialog1 extends RecyclerView.Adapter<rvadapter_initreg_dialog1.box> {
    private InfoHistoryPage.removeRV callback;
    private ArrayList<String> names, dates;
    private enum option {SURGERY, TRANSFUSION}
    private option val;

//    Constructor
    public rvadapter_initreg_dialog1(InfoHistoryPage.removeRV in_callback, ArrayList<String> in_names, ArrayList<String> in_dates){
        callback = in_callback;
        names = in_names;
        dates = in_dates;
        val = option.SURGERY;
    }
    public rvadapter_initreg_dialog1(InfoHistoryPage.removeRV in_callback, ArrayList<String> in_dates){
        callback = in_callback;
        dates = in_dates;
        val = option.TRANSFUSION;
    }

    @NonNull
    @Override
    public box onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_initreg_dialog1,
                parent,false);
        box b = new box(view);
        return b;
    }

    @Override
    public void onBindViewHolder(@NonNull box holder, int position) {
        switch (val){
            case SURGERY:
                holder.mName.setText(names.get(position));
                holder.mDate.setText(dates.get(position));
                break;
            case TRANSFUSION:
                holder.mName.setText("Past Transfusion");
                holder.mDate.setText(dates.get(position));
                break;
            default:
                ;
        }
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class box extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mName, mDate, mNamelabel;
        ImageButton mRemove;

        public box(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.rv_dialog1_name);
            mNamelabel = itemView.findViewById(R.id.event);
            mDate = itemView.findViewById(R.id.rv_dialog1_date);
            mRemove = itemView.findViewById(R.id.rv_dialog1_cancel);
            mRemove.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (v.getId() == R.id.rv_dialog1_cancel){
                switch (val){
                    case SURGERY:
                        callback.rfSurgery(pos);
                        break;
                    case TRANSFUSION:
                        callback.rfTransfusion(pos);
                        break;
                    default:
                        ;
                }
                notifyItemRemoved(pos);
            }
        }
    }
}
