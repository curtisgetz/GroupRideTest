package com.curtisgetz.groupridetest.model;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

public class Rider {

    private String mName;
    private double mLat;
    private double mLong;
    private long mUpdated;
    private String mFirebaseUserId;
    private Uri mProfileImageUri;

    public Rider(){

    }

    public Rider(FirebaseUser user){
        mName = user.getDisplayName();
        mFirebaseUserId = user.getUid();
        mProfileImageUri = user.getPhotoUrl();
    }

    public Rider(String name, double lat, double lng){
        mName = name;
        mLat = lat;
        mLong = lng;
    }

    public Rider(String name){
        mName = name;
    }

    public long getmUpdated() {
        return mUpdated;
    }

    public void setmUpdated(long mUpdated) {
        this.mUpdated = mUpdated;
    }



    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLong() {
        return mLong;
    }

    public void setmLong(double mLong) {
        this.mLong = mLong;
    }

    public String getmFirebaseUserId() {
        return mFirebaseUserId;
    }

    public void setmFirebaseUserId(String mFirebaseUserId) {
        this.mFirebaseUserId = mFirebaseUserId;
    }

    public Uri getmProfileImageUri() {
        return mProfileImageUri;
    }

    public void setmProfileImageUri(Uri mProfileImageUri) {
        this.mProfileImageUri = mProfileImageUri;
    }
}
