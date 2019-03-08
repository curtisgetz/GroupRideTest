package com.curtisgetz.groupridetest.model;

class MeetingLocation {

    private String mName;
    private double mLat;
    private double mLong;

    public MeetingLocation() {
    }

    public MeetingLocation(String mName, double mLat, double mLong) {
        this.mName = mName;
        this.mLat = mLat;
        this.mLong = mLong;
    }

    public MeetingLocation(double mLat, double mLong) {
        this.mLat = mLat;
        this.mLong = mLong;
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
}
