package com.example.uhealth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private List<UpdatedPhoto> mPhotoList;
    private FireBaseInfo mFireBaseInfo;
    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_updater_unit,null);
        PhotoAdapter.ViewHolder holder=new PhotoAdapter.ViewHolder(view);
        return holder;

    }
    public PhotoAdapter(List<UpdatedPhoto>PhotoList){
        mPhotoList=PhotoList;
        mFireBaseInfo= new FireBaseInfo();
    }
    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, int position){
        final UpdatedPhoto mUpdatedPhoto=mPhotoList.get(position);
       // holder.PhotoTitleView.setText(mUpdatedPhoto.getPhotoTitle());
        holder.PhotoView.setImageBitmap(mUpdatedPhoto.getPhoto());

    }
    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView PhotoView;
        //TextView PhotoTitleView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);//btnAppointmentUpdater
            PhotoView = itemView.findViewById(R.id.photo_image);
           // PhotoTitleView=  itemView.findViewById(R.id.photo_title);
        }

    }
}
