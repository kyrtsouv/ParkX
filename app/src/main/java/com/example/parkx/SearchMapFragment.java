package com.example.parkx;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.example.parkx.utils.ParkingSpot;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.LocalDateTime;
import java.util.List;

public class SearchMapFragment extends MapFragment {
    private Marker marker_P = null;
    private Circle circle = null;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        super.onMapReady(googleMap);

        mMap.setOnMapLongClickListener(latLng -> {
            if (marker_M != null)
                marker_M.remove();

            if (circle != null)
                circle.remove();
            marker_M = mMap.addMarker(new MarkerOptions().position(latLng));
        });

        mMap.setOnMarkerClickListener(marker -> {
            if (marker.equals(marker_M))
                bottomMapSearch(marker);
            else if (marker.equals(marker_P))
                bottomMapParking(marker);
            return false;
        });
    }

    private void checkParking(Marker marker) {
        LatLng temp = marker.getPosition();

        //ακτινα κυκλου γύρο από το χρηστη
        int RADIUS = 1000;
        circle = mMap.addCircle(new CircleOptions().center(temp).radius(RADIUS)
                .strokeColor(Color.CYAN).fillColor(0x220000FF).strokeWidth(3));

        /// λειτουργεί σωστά ως προς τη βάση
        SupabaseManager.getSpots(temp.latitude, temp.longitude, LocalDateTime.now(), new JavaResultCallback<>() {
            @Override
            public void onSuccess(List<ParkingSpot> value) {
                for (ParkingSpot p : value) {
                    LatLng ParkingLocationXY = new LatLng(p.getLatitude(), p.getLongitude());
                    MarkerOptions ParkingMarker = new MarkerOptions().position(ParkingLocationXY)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    marker_P = mMap.addMarker(ParkingMarker);
                }
            }

            @Override
            public void onError(@NonNull Throwable exception) {
                Toast.makeText(getContext(), "Αδυναμία Εκτέλεσης ... Ελέγξτε τη σύνδεσή σας", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bottomMapSearch(Marker marker) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText(marker.getTitle());
        actionButton.setText("Αναζήτηση Θέσης Πάρκινγκ");
        actionButton.setOnClickListener(v -> {
            bottomDialog.dismiss();
            Toast.makeText(getContext(), "Αναζήτηση ... Παρακαλώ Περιμένετε",
                    Toast.LENGTH_SHORT).show();
            checkParking(marker);
        });

        bottomDialog.setContentView(view);
        bottomDialog.show();
    }

    private void bottomMapParking(Marker marker) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText(marker.getTitle());
        actionButton.setText("Σύζευξη");
        actionButton.setOnClickListener(v -> {
            bottomDialog.dismiss();
            Toast.makeText(getContext(), "Εστάλει Αίτημα Σύζευξης",
                    Toast.LENGTH_SHORT).show();
            marker_M.remove();
            circle.remove();
            /// πρεπει να στελνετε ειδοποιηση για τη θεση παρκινγκ
        });

        bottomDialog.setContentView(view);
        bottomDialog.show();
    }
}
