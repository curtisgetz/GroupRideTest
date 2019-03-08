package com.curtisgetz.groupridetest.ui.main;

import android.util.Log;

import com.curtisgetz.groupridetest.model.Rider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {


    private static final String TAG = "Curt";
    private MutableLiveData<FirebaseUser> mUser = new MutableLiveData<>();
    private MutableLiveData<Rider> mRider = new MutableLiveData<>();

    public UserViewModel() {
        checkFirebaseUser();
    }

/*    public MutableLiveData<FirebaseUser> getUser(){
        return mUser;
   }*/

   public MutableLiveData<Rider> getRider(){
        return mRider;
   }

   public void removeRider(){
        mRider.postValue(null);
   }

  /* public void removeUser(){
        mUser.postValue(null);
   }*/

   public void checkFirebaseUser(){
       Log.d(TAG, "Check Firebase User");
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       if(user != null) {
           Log.d(TAG, "FB User is NOT null");
          // mUser.postValue(user);
           mRider.postValue(new Rider(user));
       }else {
           Log.d(TAG, "FB User IS null");
           removeRider();
       }
   }

}
