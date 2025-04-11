package com.example.myapplication;

import com.google.firebase.database.FirebaseDatabase;

public class MyFirebaseApp extends android.app.Application  {

    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
