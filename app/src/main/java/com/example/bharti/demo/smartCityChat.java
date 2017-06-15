package com.example.bharti.demo;

import android.app.Application;

import com.firebase.client.Firebase;


public class smartCityChat extends Application {

    private static final String TAG = "FirebaseApp";

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User user;

    public User createUser(String email, String password, String displayName)
    {
        User user = new User();
        user.setEmailId(email);
        user.setFullName(displayName);
        user.setPassword(password);
        return user;
    }

   @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}