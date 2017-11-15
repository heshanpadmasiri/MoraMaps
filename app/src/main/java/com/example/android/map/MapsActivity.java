package com.example.android.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean mPermissionDenied;
    private GoogleMap mMap;
    private ImageButton return_button;
    private ImageButton search_button;
    private LatLng university;
    private Marker marker;
    private final int defaultZoom = 17;
    public HashMap<String, Marker> locationMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        university = new LatLng(6.796877, 79.9017781);
        return_button = (ImageButton) findViewById(R.id.return_button);
        search_button = (ImageButton) findViewById(R.id.search_button);
        locationMap = new HashMap<>();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (marker != null) {
            marker.remove();
        }
    }

    public void makeLocationMap() {
        ArrayList<String> rawData = new ArrayList<>();
        rawData.addAll(Arrays.asList(getResources().getStringArray(R.array.positions)));

        String[] temp;
        double lat;
        double longitude;
        String floor;
        String name;
        String type;

        for (String data : rawData) {
            temp = data.split("_");
            lat = Double.parseDouble(temp[0]);
            longitude = Double.parseDouble(temp[1]);
            floor = (temp[2].equals("0")) ? "Ground floor" : "floor : " + temp[2];
            if (temp[2].equals("-1")) {
                floor = "";
            }
            name = temp[3];
            type = temp[4];
            Marker marker = makeMarker(lat, longitude, floor, name, type);
            locationMap.put(name, marker);
        }
    }

    public Marker makeMarker(double lat, double lon, String floor, String name, String type) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_black_24dp);
        if (type.equals("faculty")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_account_balance_black_18dp);
        }
        if (type.equals("library")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_import_contacts_black_18dp);
        }
        if (type.equals("woods")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_spa_black_18dp);
        }
        if (type.equals("washroom")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_wc_black_18dp);
        }
        if (type.equals("gates")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_exit_to_app_black_18dp);
        }
        if (type.equals("division")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_business_black_18dp);
        }
        if (type.equals("health")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_add_box_black_18dp);
        }
        if (type.equals("lab")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_airplay_black_18dp);
        }
        if (type.equals("canteen")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_black_18dp);
        }
        if (type.equals("sport")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_run_black_18dp);
        }
        if (type.equals("lecture hall")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_group_work_black_18dp);
        }
        if (type.equals("hostel")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_panorama_fish_eye_black_18dp);
        }
        if (type.equals("bank")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_account_balance_wallet_black_18dp);
        }
        if (type.equals("departments")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_all_out_black_18dp);
        }
        if (type.equals("exam hall")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_assignment_turned_in_black_18dp);
        }


        Marker marker = mMap.addMarker(new MarkerOptions()
                .icon(icon)
                .position(new LatLng(lat, lon))
                .title(name)
                .snippet(floor));
        return marker;

    }

    public void startSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String location = data.getStringExtra("location");
                showMarker(locationMap.get(location));
            }
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.

            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();

                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set the map style
        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style_2));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Move the Camera to Universtiy of Moratuwa on Starting of the application

        moveCamera(university,defaultZoom);
        makeLocationMap();
        // enable return button
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCamera(university,defaultZoom);
            }
        });

        // enable search button
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });

        // enable my location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }



        mMap.setContentDescription("Map of University of Moratuwa");

    }

    public void showMarker(Marker marker){
        moveCamera(marker.getPosition(),19);
        marker.showInfoWindow();
    }

    public void moveCamera(LatLng latLng, int zoomSize){
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, zoomSize);
        mMap.animateCamera(location);
    }
}
