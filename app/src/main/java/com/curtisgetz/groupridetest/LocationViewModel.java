package com.curtisgetz.groupridetest;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.curtisgetz.groupridetest.model.GroupRide;
import com.curtisgetz.groupridetest.model.Rider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LocationViewModel extends AndroidViewModel {

    private static final String TAG = "Curt";
    private String mLocationProvider = LocationManager.NETWORK_PROVIDER;
    private LocationManager mLocationManager;
    private CollectionReference mColRef = FirebaseFirestore.getInstance().collection("sampleGroups/group1/riders/");
    private DocumentReference mDocRef; // = FirebaseFirestore.getInstance().document("sampleGroups/group1");
    private Rider mRider;
    private LocationListener mLocListener;
    private GroupRide mGroupRide = new GroupRide();

    private MutableLiveData<Rider> mLiveDataRider = new MutableLiveData<>();
    private MutableLiveData<GroupRide> mLiveDataGroup = new MutableLiveData<>();


    public LocationViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "VM created");
        mLocationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setRider(Rider rider){
        Log.d(TAG, "rider set");
        mRider = rider;
        mLiveDataRider.postValue(rider);
        mDocRef = mColRef.document(mRider.getmFirebaseUserId());
    }

    @SuppressLint("MissingPermission")
    public void setListeners() {
        if(mRider == null) return;
        Log.d(TAG, "Registering Location Listeners");
        mLocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mRider.setmLat(location.getLatitude());
                mRider.setmLong(location.getLongitude());
                mRider.setmUpdated(location.getTime());
                mLiveDataRider.postValue(mRider);
                mDocRef.set(mRider).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "Successful Update - " + location.getLatitude() + " , " + location.getLongitude());
                        getGroupLocations();

                    }
                });
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, mLocListener);
    }

    public void getGroupLocations(){
        mColRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null || snapshots == null){
                    Log.e(TAG, "Listen failed");
                    return;
                }
                List<Rider> riderList = new ArrayList<>();
                for(QueryDocumentSnapshot doc : snapshots){
                    riderList.add(doc.toObject(Rider.class));
                }
                mGroupRide.setmRiders(riderList);
                mLiveDataGroup.postValue(mGroupRide);
            }
        });

       /* mColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    GroupRide groupRide = new GroupRide();
                    List<Rider> riderList = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        riderList.add(document.toObject(Rider.class));
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                    groupRide.setmRiders(riderList);
                    Log.d(TAG, groupRide.getmRiders().get(0).getmName());
                }else {

                }
            }
        });*/
    }

    public LiveData<GroupRide> getGroup(){
        return mLiveDataGroup;
    }

    public LiveData<Rider> getRider(){
        Log.d(TAG, "get rider");
        return mLiveDataRider;
    }

    public void unregisterListeners(){
        if(mLocListener == null) return;
        Log.d(TAG, "Unregister Location Listeners");
        mLocationManager.removeUpdates(mLocListener);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        unregisterListeners();
    }
}
