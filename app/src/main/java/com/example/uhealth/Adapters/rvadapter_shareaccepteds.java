package com.example.uhealth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.Activity.ShareFeature;
import com.example.uhealth.DataModel.Share_accepted_item;
import com.example.uhealth.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class rvadapter_shareaccepteds extends RecyclerView.Adapter<rvadapter_shareaccepteds.accepted_VH> {
    private List<Share_accepted_item> acceptedList;
    private Context mContext;

    public rvadapter_shareaccepteds(Context incontext, List<Share_accepted_item> inlist){
//        constructor
        mContext = incontext;
        acceptedList = inlist;
    }

    @NonNull
    @Override
    public accepted_VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_share_accepted_item, parent, false);
        accepted_VH vh = new accepted_VH(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull accepted_VH holder, int position) {
        Share_accepted_item item = acceptedList.get(position);
        String title = "View: "+item.getTo_username();
        holder.mTitle.setText(title);
        holder.mEmail.setText(item.getTo_email());

        String dateAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(item.getExpire() * 1000L));
        String expire = "Expire on: "+dateAsText;
        holder.mExpire.setText(expire);

        if (item.ismInfoandHistory()){
            holder.mInfoandHistory.setVisibility(View.VISIBLE);
        }else{
            holder.mInfoandHistory.setVisibility(View.GONE);
        }
        if (item.ismDiagnosis()){
            holder.mDiagnosis.setVisibility(View.VISIBLE);
        }else{
            holder.mDiagnosis.setVisibility(View.GONE);
        }
        if (item.ismTimeline()){
            holder.mTimeline.setVisibility(View.VISIBLE);
        }else{
            holder.mTimeline.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (acceptedList!=null){
            return acceptedList.size();
        }else{
            return 0;
        }
    }

    public class accepted_VH extends RecyclerView.ViewHolder{
        TextView mTitle, mEmail, mExpire;
        RadioGroup mRG;
        RadioButton mInfoandHistory, mDiagnosis, mTimeline;
        Button mView;

        public accepted_VH(@NonNull View itemView) {
            super(itemView);
            initview();
            initevents();
        }

        private void initevents() {
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    Share_accepted_item item = acceptedList.get(postion);
                    int rb_id = mRG.getCheckedRadioButtonId();
                    if (rb_id == -1){
                        Toast.makeText(mContext, "Must select type to View", Toast.LENGTH_SHORT).show();
                    }else{
                        RadioButton selected = (RadioButton)itemView.findViewById(rb_id);
                        String rbText = selected.getText().toString();
                        ((ShareFeature)mContext).viewFromAccepteds(rbText, item);
                    }
                }
            });
        }

        private void initview() {
            mTitle = itemView.findViewById(R.id.Accepted_title);
            mEmail = itemView.findViewById(R.id.Accepted_email);
            mRG = itemView.findViewById(R.id.Accepted_RG);
            mInfoandHistory = itemView.findViewById(R.id.Accepted_InfoandHistroy);
            mDiagnosis = itemView.findViewById(R.id.Accepted_Diagnosis);
            mTimeline = itemView.findViewById(R.id.Accepted_Timeline);
            mView = itemView.findViewById(R.id.Accepted_view);
            mExpire = itemView.findViewById(R.id.Accepted_expire);
        }
    }
}
