package com.example.uhealth.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhealth.Activity.Timeline;
import com.example.uhealth.DataModel.timeline_item;
import com.example.uhealth.R;
import com.example.uhealth.ViewHolders.viewholder_timeline;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class rvadapter_tlitem extends RecyclerView.Adapter<viewholder_timeline>{
    private List<timeline_item> itemList;

    private Context mContext;
    private Timeline.passVMtoRV listener;
    private int currentpos = -1;

    public rvadapter_tlitem(Context in_Context, Timeline.passVMtoRV inlistener, List<timeline_item> in_itemlist){
        itemList = in_itemlist;
        listener = inlistener;
        mContext = in_Context;
    }


    @NonNull
    @Override
    public viewholder_timeline onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_timeline_item, parent, false);
        viewholder_timeline tlvh = new viewholder_timeline(view, new clickListener() {
            @Override
            public void CurrentClick(int pos) {
                int lastpos = currentpos;
                currentpos = pos;
                notifyItemChanged(lastpos);
            }

            @Override
            public int getLastClcick() {
                return currentpos;
            }
        });
        return tlvh;
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder_timeline holder, int position) {
        timeline_item item = itemList.get(position);
        String myString = item.getAppointmentType();
        String upperString = myString.substring(0, 1).toUpperCase() + myString.substring(1).toLowerCase();

        holder.mAppointment.setText(upperString);
        holder.mPhysician.setText(item.getPhysicianName());
        holder.mLocation.setText(item.getApptLocation());
        holder.mNote.setText(item.getNote());

        String dateAsText = new SimpleDateFormat("yyyy-MM-dd")
                .format(new Date(item.getDate() * 1000L));
        holder.mDate.setText(dateAsText);

        if (position == currentpos){
//            only open one at a time
            holder.mCard.setCardElevation(50);
            holder.mDots.setVisibility(View.GONE);
            holder.mAdditional.setVisibility(View.VISIBLE);
        }else{
            holder.mCard.setCardElevation(0);
            holder.mDots.setVisibility(View.VISIBLE);
            holder.mAdditional.setVisibility(View.GONE);
        }

        List<String> l_meds = item.getmMedls();
        NestedRVAdp_meds medsAdp = new NestedRVAdp_meds(l_meds);
        RecyclerView.LayoutManager medsLayoutManager = new GridLayoutManager(mContext, 1);
        holder.mMeds.setLayoutManager(medsLayoutManager);
        holder.mMeds.setAdapter(medsAdp);
        holder.mMeds.setHasFixedSize(true);

        List<String> l_photos = item.getImagePaths();
        NestedRVAdp_photos photosAdp = new NestedRVAdp_photos(l_photos, mContext);
        RecyclerView.LayoutManager photosLayoutManager = new GridLayoutManager(mContext, 2);
        holder.mPhotos.setLayoutManager(photosLayoutManager);
        holder.mPhotos.setAdapter(photosAdp);
        holder.mPhotos.setHasFixedSize(true);

//        filter hide view by appointment type
        if (listener.getfiltertypes().contains(item.getAppointmentType())){
            holder.itemView.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.itemView.setLayoutParams(params);
        }else{
            holder.itemView.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height = 0;
            holder.itemView.setLayoutParams(params);
        }

//        set color by appointment type
        String type = item.getAppointmentType();
        Drawable color;
        switch (type){
            case "office appointment":{
                color = mContext.getResources().getDrawable(R.drawable.tldot_lightgreen);
//                color = mContext.getResources().getColor(R.color.lightgreen);
                break;
            }
            case "chemotherapy":{
                color = mContext.getResources().getDrawable(R.drawable.tldot_lightpink);
//                color = mContext.getResources().getColor(R.color.lightpink);
                break;
            }
            case "surgery or procedure appointment" :{
                color = mContext.getResources().getDrawable(R.drawable.tldot_lightteal);
//                color = mContext.getResources().getColor(R.color.lightteal);
                break;
            }
            case "radiation session":{
                color = mContext.getResources().getDrawable(R.drawable.tldot_lightviolet);
//                color = mContext.getResources().getColor(R.color.lightviolet);
                break;
            }
            case "imaging appointment":{
                color = mContext.getResources().getDrawable(R.drawable.tldot_lightyellow);
//                color = mContext.getResources().getColor(R.color.lightyellow);
                break;
            }
            default:
                color = mContext.getResources().getDrawable(R.drawable.tldot_lightgreen);
//                color = mContext.getResources().getColor(R.color.orangeDark);
                break;
        }
//        holder.mPhysician.setBackgroundColor(color);
//        holder.mAppointment.setBackgroundColor(color);
        holder.mtldot.setBackground(color);
    }

    @Override
    public int getItemCount() {
        if (itemList!=null){
            return itemList.size();
        }else{
            return 0;
        }
    }

    public interface clickListener{
        void CurrentClick(int pos);
        int getLastClcick();
    }

}
