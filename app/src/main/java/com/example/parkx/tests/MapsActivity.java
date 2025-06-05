package com.example.parkx.tests;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.parkx.R;
import com.example.parkx.databinding.ActivityMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int Location_Permission_Code = 1;
    private final int RADIUS = 800;//ακτινα κυκλου γύρο από το χρηστη
    private GoogleMap mMap;
    private ActivityMapBinding binding;
    private FusedLocationProviderClient locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationProvider = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Location_Permission_Code);
            return;
        }

        mMap.setMyLocationEnabled(true);

        locationProvider.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng locationXY = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(locationXY).title("My Location: " + locationXY));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationXY, 13f));

                Circle circle = mMap.addCircle(new CircleOptions().center(locationXY).radius(RADIUS).strokeColor(Color.CYAN).fillColor(0x220000FF).strokeWidth(3));

                /*for(Person p: list){
                    LatLng ParkingLocationXY = new LatLng(p.getLocationX(),p.getLocationY());
                    MarkerOptions ParkingMarker=new MarkerOptions().position(ParkingLocationXY)
                            .title(p.getName()+" Location : X= "+ p.getLocationX()+", Y= "+ p.getLocationY());

                    totalMarker=mMap.addMarker(ParkingMarker);

                    //double distance = haversineDistance(locationXY, ParkingMarker.getPosition() );
                    double distance = SphericalUtil.computeDistanceBetween(locationXY,ParkingLocationXY);

                    assert totalMarker != null;
                    //totalMarker.setVisible(distance < RADIUS);
                    totalMarker.setVisible(true);*/
                /// για κάθε τοποθεσία στο χάρτη ενεργοποιώ "κουμπί" που να δείχνει για Αίτημα Σύζευξης
                mMap.setOnMarkerClickListener(marker -> {
                    bottomMap(marker);
                    return false; // + να δείχνει το default info window
                });
            } else {
                Toast.makeText(this, "Ενεργοποίησε το GPS", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MapsActivity.this, BasicMenu.class));
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void bottomMap(Marker marker) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText(marker.getTitle());
        actionButton.setOnClickListener(v -> {
            bottomDialog.dismiss();
            Toast.makeText(this, "Αίτηση Σύζευξης ... Παρακαλώ Περιμένετε", Toast.LENGTH_SHORT).show();

            ///check if Parking accepted or Not

        });

        bottomDialog.setContentView(view);
        bottomDialog.show();
    }
}