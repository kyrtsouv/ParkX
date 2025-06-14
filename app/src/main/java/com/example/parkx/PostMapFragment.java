package com.example.parkx;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PostMapFragment extends MapFragment {
    private boolean bottomMapPostVisible = false;


    @SuppressLint("CutPasteId")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       //It loads the visibility state of bottomMapPost
        if (savedInstanceState != null)
            bottomMapPostVisible = savedInstanceState.getBoolean("bottomMapPostVisible", false);
    }

    //It prepares the map by removing the previous marker creates a new one (and sets its click listener) with the new coordinates and sets the click

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        super.onMapReady(googleMap);

        // Restores the marker from the bundle
        mMap.setOnMapLongClickListener(latLng -> {
            if (marker_M != null) {
                marker_M.remove();
            }
            marker_M = mMap.addMarker(new MarkerOptions().position(latLng));
        });

        mMap.setOnMarkerClickListener(marker -> {
            bottomMapPost(marker);
            return false;
        });

        if (bottomMapPostVisible) {
            bottomMapPost(marker_M);
        }
    }


    //   Pop up menu that sends the coordinates of the marker that the user made with the chosen dateTime ( if no dateTime is chosen it uses the system's)
    //  to the database

    public void bottomMapPost(Marker marker) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText(R.string.parking_spot_question);
        actionButton.setText(R.string.add_parking_spot);
        actionButton.setOnClickListener(v -> {
            bottomDialog.cancel();
            LatLng temp = marker.getPosition();

        //The method publish spot is called with the coordinates and dateTime
            SupabaseManager.publishSpot(temp.latitude, temp.longitude, dateTime, new JavaResultCallback<>() {
                @Override
                public void onSuccess(String value) {
                    Toast.makeText(getContext(), R.string.parking_spot_added, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull Throwable exception) {
                    Toast.makeText(getContext(), "Something went wrong ... lease check your connection", Toast.LENGTH_SHORT).show();
                }
            });
        });
        bottomDialog.setOnCancelListener(dialog -> bottomMapPostVisible = false);
        bottomDialog.setContentView(view);
        bottomDialog.getBehavior().setPeekHeight(1000);
        bottomDialog.show();
        bottomMapPostVisible = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("bottomMapPostVisible", bottomMapPostVisible);
    }
}
