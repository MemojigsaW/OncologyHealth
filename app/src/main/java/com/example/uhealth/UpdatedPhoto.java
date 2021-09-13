package com.example.uhealth;

import android.graphics.Bitmap;

public class UpdatedPhoto {
    private String PhotoTitle;//without ".jpg"
    private Bitmap Photo;
    public UpdatedPhoto(String title,Bitmap bm){
        PhotoTitle = title;
        Photo = bm;
    }
    public String getPhotoTitle(){
        return this.PhotoTitle;
    }
    public void setPhotoTitle(String title){
        this.PhotoTitle = title;
    }
    public Bitmap getPhoto(){
        return this.Photo;
    }
    public void setPhoto(Bitmap photo){
        this.Photo = photo;
    }


}
