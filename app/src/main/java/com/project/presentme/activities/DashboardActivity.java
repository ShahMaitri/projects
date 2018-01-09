package com.project.presentme.activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.project.presentme.R;
import com.project.presentme.fragments.DistanceFragment;
import com.project.presentme.fragments.HistoryFragment;
import com.project.presentme.fragments.LocationFragment;
import com.project.presentme.utils.Constants;
import com.project.presentme.utils.DatabaseUtils;
import com.project.presentme.utils.Distance;

import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity implements LocationListener {

    private LocationListener locationListener;
    private LocationManager locationManager;
    private Context context;
    private String provider;
    private Location myLocation;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        initUI();

    }

    private void initUI() {
        context = this;
        locationListener = this;
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        if (Constants.DISTANCE == 0.0d) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.PREF_NOTIF, false);
            editor.apply();
        }
        callLocationTab();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("LOCATION"));
        tabLayout.addTab(tabLayout.newTab().setText("DISTANCE"));
        tabLayout.addTab(tabLayout.newTab().setText("HISTORY"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (pos == 0) {
                    callLocationTab();
                } else if (pos == 1) {
                    callDistanceTab();
                } else if (pos == 2) {
                    callHistoryTab();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void callLocationTab() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.tabs_details, new LocationFragment()).commit();

        if (myLocation == null)
        updateLocationData();
    }

    private void callDistanceTab() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.tabs_details, new DistanceFragment()).commit();

        if (myLocation == null)
            updateLocationData();
    }

    private void callHistoryTab() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.tabs_details, new HistoryFragment()).commit();

        if (myLocation == null)
            updateLocationData();

    }

    private void updateLocationData() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Get the location manager
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        myLocation = location;
        updateDistanceData(location);
        System.out.println("Provider " + provider + " has been selected.");
        // Initialize the location fields
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.tabs_details);
        if (fragment instanceof LocationFragment) {
            ((LocationFragment) fragment).updateCurrentLocation(location);
        }

        requestUpdate();
    }

    private void requestUpdate() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (locationManager != null)
            locationManager.requestLocationUpdates(provider, 400, 1, locationListener);
    }

    private void updateDistanceData(Location mCurrentLocation){
        if (mCurrentLocation != null) {
            Constants.CURRENT_LAT = mCurrentLocation.getLatitude();
            Constants.CURRENT_LONG = mCurrentLocation.getLongitude();

            if (Constants.START_LAT == 0.0)
                Constants.START_LAT = mCurrentLocation.getLatitude();

            if (Constants.START_LONG == 0.0)
                Constants.START_LONG = mCurrentLocation.getLongitude();

            Constants.DISTANCE = meterDistanceBetweenPoints(Constants.START_LAT, Constants.START_LONG,
                    Constants.CURRENT_LAT, Constants.CURRENT_LONG);

            Distance distance = new Distance();
            distance.setDistDateTime(Calendar.getInstance().getTime().toString());
            distance.setDistValue(String.valueOf(Constants.DISTANCE));

            if (Constants.DISTANCE >= 50) {
                if (!sharedPreferences.contains(Constants.PREF_NOTIF) || !sharedPreferences.getBoolean(Constants.PREF_NOTIF, false)) {
                    createNotification();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constants.PREF_NOTIF, true);
                    editor.apply();
                }
                new DatabaseUtils(context).insertDistance(distance);
            }


        }
    }

    public void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, DashboardActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Distance")
                .setContentText("You have travelled more than 50 mtrs.").setSmallIcon(R.mipmap.ic_launcher)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

    }

    private double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        float pk = (float) (180.f/Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null)
            locationManager.removeUpdates(this);
    }

    /* Request updates at startup */
    @Override
    public void onResume() {
        super.onResume();
        requestUpdate();
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        updateDistanceData(location);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.tabs_details);
        if (fragment instanceof LocationFragment) {
            ((LocationFragment) fragment).updateCurrentLocation(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(context, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

}
