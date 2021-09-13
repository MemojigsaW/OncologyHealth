package com.example.uhealth.ViewHolders;

import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.Adapters.rvadapter_tlitem;
import com.example.uhealth.R;

public class viewholder_timeline extends RecyclerView.ViewHolder {
    public ConstraintLayout mTotalLayout;
    public CardView mCard;
    public TextView mDate, mAppointment, mPhysician, mLocation, mNote;
    public RecyclerView mMeds, mPhotos;

    public ImageView mDots;
    public RelativeLayout mAdditional;
    public View mtldot;
    private rvadapter_tlitem.clickListener clickerlistener;

    public viewholder_timeline(@NonNull View itemView, rvadapter_tlitem.clickListener clickListener) {
        super(itemView);
        clickerlistener =clickListener;
        initView();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                int lastpos = clickListener.getLastClcick();
                if (lastpos!=pos){
                    clickerlistener.CurrentClick(pos);
                    int cur_vis = mDots.getVisibility();
                    if (cur_vis == View.VISIBLE){
                        mCard.setCardElevation(50);
                        mDots.setVisibility(View.GONE);
                        mAdditional.setVisibility(View.VISIBLE);
                    }else{
                        mCard.setCardElevation(0);
                        mDots.setVisibility(View.VISIBLE);
                        mAdditional.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void initView() {
        mCard = itemView.findViewById(R.id.cardView);
        mTotalLayout = itemView.findViewById(R.id.tl_item_layout);
        mDate = itemView.findViewById(R.id.date);
        mAppointment = itemView.findViewById(R.id.appointment_type);
        mPhysician = itemView.findViewById(R.id.physician_name);
        mLocation = itemView.findViewById(R.id.location);
        mDots = itemView.findViewById(R.id.dots);
        mtldot = itemView.findViewById(R.id.tl_dot);

        mAdditional = itemView.findViewById(R.id.additional_info);
        mNote = itemView.findViewById(R.id.note);
        mMeds = itemView.findViewById(R.id.medications_rv);
        mPhotos = itemView.findViewById(R.id.photos_rv);
    }
}
