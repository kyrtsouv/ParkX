package com.example.parkx;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class Map_Fragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_map, container, false);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);

        return view;
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


        LatLng athens = new LatLng(37.9838, 23.7275);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(athens, 10));

        mMap.setOnMapClickListener(latLng ->
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Σημείο"+latLng.latitude+" "+latLng.longitude))
        );
        mMap.setOnMarkerClickListener(marker -> {
            bottomMap2(marker);
            return false;
        });

    }
    @SuppressLint("SetTextI18n")
    public void bottomMap2(Marker marker){
        Log.d("MapFragment", "Marker clicked: " + marker.getTitle());
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText(marker.getTitle());
        actionButton.setText("Προσθήκη Θέσης Πάρκινγκ");
        actionButton.setOnClickListener(v -> {
            bottomDialog.dismiss();
            //add Parking location in list or database
            //LatLng loc=marker.getPosition();
            Toast.makeText(getContext(), "Προστέθηκε η Θέση Πάρκινκ",
                    Toast.LENGTH_SHORT).show();
        });
        bottomDialog.setContentView(view);
        bottomDialog.show();
    }
}
