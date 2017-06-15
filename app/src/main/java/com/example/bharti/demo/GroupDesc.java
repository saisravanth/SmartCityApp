package com.example.bharti.demo;

import android.graphics.Bitmap;


public class GroupDesc {
    private Bitmap thumbnail;
    private String description;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    private String dateTime;
    private String status;

    private String id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GroupDesc( String title, String desc) {
        this.description = title;
        this.dateTime = desc;
    }

    public GroupDesc() {

    }
    public Bitmap getImageId() {
        return thumbnail;
    }
    public void setImageId(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return getDescription() + "\n" + getDateTime();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
