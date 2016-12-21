package com.apps.dilip_pashi.letsgo;

import android.net.Uri;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Dilip_pashi on 05-12-2016.
 */

public class LetsGo extends MultiDexApplication {

    public static final String USERS = "Users";
    public static final String DETAILS = "Details";
    public static FirebaseDatabase mDatabase;
    public static DatabaseReference mReference;
    public static FirebaseAuth auth;
    public static FirebaseUser mFirebaseUser;
    public static String mUsername;
    public static Uri UserPic;
    //public static FirebaseStorage storage;
    public static StorageReference storageRef;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        MultiDex.install(this);

    }
}
