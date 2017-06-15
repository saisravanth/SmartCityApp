package com.example.bharti.demo;

import android.location.Location;

import java.util.Date;

public class Issue {
    String user_id;
    String category;
    String date;
    String description;
    String image;
    String location;
    String status;

//    public Issue(String category, Date date, String description, String location){
//        this.category = category;
//        this.date = date;
//        this.description = description;
//        this.location = location;
//    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public String getUser_id() { return user_id;}

    public void setUser_id(String user_id) { this.user_id = user_id; }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() { return status;}

    public void setStatus(String status) {this.status = status;}
}
