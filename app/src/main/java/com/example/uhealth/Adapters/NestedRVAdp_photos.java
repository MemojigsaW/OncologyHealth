package com.example.uhealth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.uhealth.Activity.DisplayFullImage;
import com.example.uhealth.R;
import com.example.uhealth.utils.FireBaseInfo;
import com.example.uhealth.utils.GlideApp;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class NestedRVAdp_photos extends RecyclerView.Adapter<NestedRVAdp_photos.VH_Nestedrv_photos> {
    private List<String> pathlist;
    private FireBaseInfo mFireBaseInfo;
    private Context mContext;

    public NestedRVAdp_photos(List<String> input, Context inContext){
        pathlist = input;
        mFireBaseInfo = new FireBaseInfo();
        mContext = inContext;
    }

    @NonNull
    @Override
    public VH_Nestedrv_photos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nestedrv_photos, parent, false);
        VH_Nestedrv_photos vh = new VH_Nestedrv_photos(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Nestedrv_photos holder, int position) {
        String path = pathlist.get(position);
        StorageReference pathRef = mFireBaseInfo.mStorageRef.child(path);

        GlideApp.with(mContext)
                .load(pathRef)
                .apply(new RequestOptions().override(holder.mPhoto.getWidth(), holder.mPhoto.getHeight()))
                .into(holder.mPhoto);
    }

    @Override
    public int getItemCount() {
        if (pathlist!=null){
            return pathlist.size();
        }else{
            return 0;
        }
    }

    public class VH_Nestedrv_photos extends RecyclerView.ViewHolder {
        ImageView mPhoto;

        public VH_Nestedrv_photos(@NonNull View itemView) {
            super(itemView);
            mPhoto = itemView.findViewById(R.id.photoThumbnail);

            mPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openimage = new Intent(mContext, DisplayFullImage.class);
                    String path = pathlist.get(getAdapterPosition());
                    openimage.putExtra("IMAGE_PATH", path);
                    mContext.startActivity(openimage);
                }
            });
        }
    }

}
