package com.curtisgetz.groupridetest.model;

import java.util.List;

public class GroupRide {

    private Rider mRideOwner;
    private List<Rider> mRiders;
    private MeetingLocation mMeetingLocation;


    public GroupRide() {
    }

    public GroupRide(Rider mRideOwner) {
        this.mRideOwner = mRideOwner;
    }

    public GroupRide(Rider mRideOwner, List<Rider> mRiders) {
        this.mRideOwner = mRideOwner;
        this.mRiders = mRiders;
    }

    public Rider getmRideOwner() {
        return mRideOwner;
    }

    public void setmRideOwner(Rider mRideOwner) {
        this.mRideOwner = mRideOwner;
    }

    public List<Rider> getmRiders() {
        return mRiders;
    }

    public void setmRiders(List<Rider> mRiders) {
        this.mRiders = mRiders;
    }
}
