package com.curtisgetz.groupridetest.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.curtisgetz.groupridetest.LocationViewModel;
import com.curtisgetz.groupridetest.MapFragment;
import com.curtisgetz.groupridetest.R;
import com.curtisgetz.groupridetest.model.Rider;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "Curt";
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 100;

    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    private final static int RC_SIGN_IN = 100;
    private boolean isSignedIn = false;
    private LocationViewModel mLocViewModel;
    private UserViewModel mUserViewModel;


    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.login_status)
    ImageView mLoginStatus;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    private ImageView mNavUserImage;
    private TextView mNavUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_nav_menu);

        mLocViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
        setNavItemSelectedListener();
        setDrawerListener();

        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUserViewModel.getRider().observe(this, rider -> {
            updateRiderUI(rider);
        });
        //setNavHeaderViews();
    }

    private void setNavItemSelectedListener(){
        mNavView.setNavigationItemSelectedListener(item -> {
           // item.setChecked(true);
            mDrawerLayout.closeDrawers();
            switch (item.getItemId()){
                case R.id.sign_in_sign_out:
                    onLoginClick();
            }
            //UI Code Here:
            return true;
        });
    }

    private void setDrawerListener(){
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //setNavHeaderViews();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }


    private void setNavHeaderViews(){
        Rider rider = mUserViewModel.getRider().getValue();
        View headerView = mNavView.getHeaderView(0);
        mNavUserName = headerView.findViewById(R.id.user_name_nav);
        mNavUserImage = headerView.findViewById(R.id.user_image_nav);
        Menu menu = mNavView.getMenu();
        MenuItem signInItem = menu.findItem(R.id.sign_in_sign_out);

        if(mNavUserName == null || mNavUserImage == null) return;

        if(rider != null){
            signInItem.setTitle(getString(R.string.sign_out));
            Picasso.get().load(rider.getmProfileImageUri()).into(mNavUserImage, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap source = ((BitmapDrawable) mNavUserImage.getDrawable()).getBitmap();
                    RoundedBitmapDrawable drawable =
                            RoundedBitmapDrawableFactory.create(getResources(), source);
                    drawable.setCircular(true);
                    drawable.setCornerRadius(Math.max(source.getWidth() / 2.0f, source.getHeight() / 2.0f));
                    mNavUserImage.setImageDrawable(drawable);
                }

                @Override
                public void onError(Exception e) {

                }
            });
            mNavUserName.setText(rider.getmName());
        } else {
            signInItem.setTitle(getString(R.string.sign_in));
            mNavUserImage.setImageResource(R.drawable.ic_signed_out);
            mNavUserName.setText(getString(R.string.not_logged_in));
        }
    }

    private void checkLocationPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST);
        }else {
            mLocViewModel.setListeners();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocViewModel.setListeners();
                }
        }
    }

    @OnClick({R.id.login_click, R.id.login_status, R.id.title_text})
    public void onLoginClick(){
        Log.e(TAG, "Clicked");
        if(!isSignedIn){
            showAuthUI();
        }else{
            signOut();
        }
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_SHORT).show();
                    isSignedIn = false;
                    setSignInIcon();
                    mTitleText.setText(getString(R.string.kick_stands_up));
                    if(mLocViewModel != null){
                        mLocViewModel.unregisterListeners();
                    }
                    mUserViewModel.removeRider();
                });

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(MapFragment.class.getSimpleName());
        if(mapFragment != null){
            Log.d(TAG, "MapFragment is not null");
            getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
        }
    }


    private void showAuthUI(){
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK){
                mUserViewModel.checkFirebaseUser();
                isSignedIn = true;
            }else {
                isSignedIn = false;
                if(response!= null && response.getError() != null){
                    Toast.makeText(this, response.getError().getErrorCode(), Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(this, "Sign in error", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void updateRiderUI(Rider rider){
        Log.d(TAG, "Sign In");
        String title = getString(R.string.kick_stands_up);
        if(rider != null){
            isSignedIn = true;
            title = title + "\nWelcome " + rider.getmName();
            mTitleText.setText(title);
            //checkLocationPermissions();
            /*MapFragment fragment = new MapFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment, fragment.getClass().getSimpleName()).commit();
       */
        }else {
            isSignedIn = false;
            Log.d(TAG, "User is null");
        }
        setNavHeaderViews();
        setSignInIcon();
    }



    private void setSignInIcon() {
        mLoginStatus.setImageResource(isSignedIn ? R.drawable.motorcyclegreen : R.drawable.motorcyclered);
    }

}
