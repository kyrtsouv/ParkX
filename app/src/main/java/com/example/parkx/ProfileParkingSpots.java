package com.example.parkx;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.example.parkx.utils.ParkingSpot;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class ProfileParkingSpots extends MapFragment {

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = view.findViewById(R.id.btn_date);
        btn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        super.onMapReady(googleMap);
        googleMap.setOnMapClickListener(v -> {
        });

        SupabaseManager.getMyParkingSpots(new JavaResultCallback<>() {
            @Override
            public void onSuccess(List<ParkingSpot> value) {
                for (ParkingSpot p : value) {
                    LatLng temp = new LatLng(p.getLatitude(), p.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(temp).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            }

            @Override
            public void onError(@NonNull Throwable exception) {
                Toast.makeText(getContext(), "Αδυναμία Εκτέλεσης Parking Spots στο Προφιλ" + " ... Ελέγξτε τη σύνδεσή σας", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
