package com.project.presentme.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.presentme.R;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment implements OnMapReadyCallback {

    private Context mContext;
    private GoogleMap map;
    private MarkerOptions currentPositionMarker = null;
    private Marker currentLocationMarker;

    private TextView tvLocation;
    Location mCurrentLocation;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        mContext = getActivity();
        tvLocation = view.findViewById(R.id.tvLocation);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();

        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(32, 75);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.setMyLocationEnabled(true);
        map.animateCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    public void updateAddress() {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());

            addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();

            tvLocation.setText(address + "\n" + city + ", " + state + "\n" + country + " - " + postalCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCurrentLocationMarker(){

        if(map != null){

            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            if(currentPositionMarker == null){
                currentPositionMarker = new MarkerOptions();

                currentPositionMarker.position(latLng)
                        .title("My Location");
                currentLocationMarker = map.addMarker(currentPositionMarker);
            }

            if(currentLocationMarker != null)
                currentLocationMarker.setPosition(latLng);

            map.setMinZoomPreference(15);
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }


    public void updateCurrentLocation(Location location) {
        mCurrentLocation = location;
        if (location != null) {
            updateAddress();
            updateCurrentLocationMarker();
        }
    }
}