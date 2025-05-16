package com.example.parkx;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.parkx.databinding.ActivityMapBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapBinding binding;
    private FusedLocationProviderClient locationProvider;
    private static final int Location_Permission_Code=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationProvider= LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Location_Permission_Code);
            return;
        }

        mMap.setMyLocationEnabled(true);

        locationProvider.getLastLocation().addOnSuccessListener(this,location -> {
            if(location!=null){
                LatLng locationXY = new LatLng(location.getLatitude(),location.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(locationXY).title("My Location"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(locationXY));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationXY,15f));
            }else {
                Toast.makeText(this, "Δεν βρέθηκε τοποθεσία", Toast.LENGTH_SHORT).show();
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}