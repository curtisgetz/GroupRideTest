package com.curtisgetz.groupridetest;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.curtisgetz.groupridetest.model.GroupRide;
import com.curtisgetz.groupridetest.model.Rider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "Curt";
    @BindView(R.id.map) View mMapView;
    @BindView(R.id.loading_map_text) TextView mMapLoadingText;

    private Unbinder mUnbinder;
    private final static float DEFAULT_MAP_ZOOM_LEVEL = 14.0f;
    private float GOOGLE_MAP_ZOOM_LEVEL = DEFAULT_MAP_ZOOM_LEVEL;
    private GoogleMap mGoogleMap;
    private LocationViewModel mViewModel;
    private boolean isFirstZoom = true;
    private boolean isMapReady;

    public MapFragment() {
        Log.d(TAG, "MapFragment created");
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() == null) return;
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.map, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_map, container, false);
        mUnbinder = ButterKnife.bind(this, view);


        mMapView.setVisibility(View.GONE);
        mViewModel = ViewModelProviders.of(getActivity()).get(LocationViewModel.class);
        mViewModel.getGroup().observe(this, new Observer<GroupRide>() {
            @Override
            public void onChanged(GroupRide groupRide) {
                setGroupMap(groupRide);
            }
        });

       /* mViewModel.getRider().observe(this, new Observer<Rider>() {
            @Override
            public void onChanged(Rider rider) {
                Log.d(TAG, "onChanged");
                setMap();
            }
        });
*/
        return view;
    }

    private void setGroupMap(GroupRide groupRide){
        Log.d(TAG, "Set Group Map");
        if(mGoogleMap == null || groupRide == null) return;
        if(mGoogleMap.getCameraPosition().zoom > DEFAULT_MAP_ZOOM_LEVEL){
            GOOGLE_MAP_ZOOM_LEVEL = mGoogleMap.getCameraPosition().zoom;
        }

        mGoogleMap.clear();
        for(Rider rider : groupRide.getmRiders()){
            LatLng latLng = new LatLng(rider.getmLat(), rider.getmLong());
            mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(rider.getmName()));
        }
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(GOOGLE_MAP_ZOOM_LEVEL));
        Rider rider = mViewModel.getRider().getValue();
        if(rider == null){
            rider = groupRide.getmRiders().get(0);
        }
        LatLng riderLatLng = new LatLng(rider.getmLat(), rider.getmLong());
        if(isFirstZoom){
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(riderLatLng));
            isFirstZoom = false;
        }

        mMapView.setVisibility(View.VISIBLE);
        mMapLoadingText.setVisibility(View.GONE);

    }

    private void setMap() {
        Log.d(TAG, "Set Map");

        Rider rider = mViewModel.getRider().getValue();
        if(mGoogleMap == null || rider == null)return;
        if(mGoogleMap.getCameraPosition().zoom > DEFAULT_MAP_ZOOM_LEVEL){
            GOOGLE_MAP_ZOOM_LEVEL = mGoogleMap.getCameraPosition().zoom;
        }
        Log.d(TAG, String.valueOf(rider.getmLat() + "  -  " + rider.getmLong()));
        LatLng latLng = new LatLng(rider.getmLat(), rider.getmLong());
        mGoogleMap.clear();
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(rider.getmName()));
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(GOOGLE_MAP_ZOOM_LEVEL));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "Map Ready");
        mGoogleMap = googleMap;
        isMapReady = true;
        //setMap();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //mGoogleMap.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
