package com.example.uhealth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.Activity.ShareFeature;
import com.example.uhealth.DataModel.Share_outstandings_item;
import com.example.uhealth.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class rvadapter_shareoutstandings extends RecyclerView.Adapter<rvadapter_shareoutstandings.outstandings_vh> {
    private List<Share_outstandings_item> outstandingslist;
    private Context mContext;

    public rvadapter_shareoutstandings(Context in_context, List<Share_outstandings_item> in_outstandinglist){
//        constructor
        outstandingslist = in_outstandinglist;
        mContext = in_context;
    }

    @NonNull
    @Override
    public outstandings_vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_share_outstandings_item, parent, false);
        outstandings_vh vh = new outstandings_vh(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull outstandings_vh holder, int position) {
        Share_outstandings_item item = outstandingslist.get(position);
        String fromuser = item.getFrom_username();
        String fromemail = item.getFrom_email();

        String dateAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(item.getExpire() * 1000L));
        holder.mEmail.setText(fromemail);
        holder.mUsername.setText("From "+fromuser);
        holder.mExpire.setText(dateAsText);
    }

    @Override
    public int getItemCount() {
        if (outstandingslist!=null){
            return outstandingslist.size();
        }else{
            return 0;
        }
    }

    public class outstandings_vh extends RecyclerView.ViewHolder {
        TextView mUsername, mEmail, mExpire;
        Button mDecline, mAccept;
        public outstandings_vh(@NonNull View itemView) {
            super(itemView);
            initview();
            initEvents();
        }

        private void initEvents() {
            mDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Share_outstandings_item item = outstandingslist.get(position);
                    String docid = item.getDocumentId();
                    ((ShareFeature)mContext).deleteOutstandingdoc(docid);
                }
            });
            mAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Share_outstandings_item item = outstandingslist.get(position);
                    ((ShareFeature)mContext).openOutstandingAcceptDialog(item);
                }
            });
        }

        private void initview() {
            mUsername = itemView.findViewById(R.id.outstanding_fromusername);
            mEmail = itemView.findViewById(R.id.outstanding_fromemail);
            mExpire = itemView.findViewById(R.id.outstanding_expiredate);
            mDecline = itemView.findViewById(R.id.outstanding_decline);
            mAccept = itemView.findViewById(R.id.outstanding_accept);
        }
    }
}
