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

import java.util.ArrayList;
import java.util.List;

public class SearchMapFragment extends MapFragment {
    private final List<Marker> markers_P = new ArrayList<>();
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
            else if (markers_P.contains(marker))
                bottomMapRequest(marker);
            return false;
        });
    }

    /// μεθοδος ελεγχου παρκινγ που καλει απο τη βαση τις συντεταγμενες ολων των marker
    /// που ειναι σε ακτινα 1000μ απο το σημειο του χρηστη (Marker)
    /// μαζι με την ωρα επιλογης , αλλιως στελενει μαζι με ωρα συστηματος
    private void checkParking(Marker marker) {
        LatLng temp = marker.getPosition();


        SupabaseManager.getSpots(temp.latitude, temp.longitude, dateTime, new JavaResultCallback<>() {
            @Override
            public void onSuccess(List<ParkingSpot> value) {
                //ακτινα κυκλου γύρο από το χρηστη 1000m
                int RADIUS = 1000;
                if (circle != null)
                    circle.remove();
                circle = mMap.addCircle(new CircleOptions().center(temp).radius(RADIUS)
                        .strokeColor(Color.CYAN).fillColor(0x220000FF).strokeWidth(3));
                for (ParkingSpot p : value) {
                    LatLng ParkingLocationXY = new LatLng(p.getLatitude(), p.getLongitude());
                    MarkerOptions ParkingMarker = new MarkerOptions().position(ParkingLocationXY)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    Marker marker = mMap.addMarker(ParkingMarker);

                    assert marker != null;
                    marker.setTag(p);
                    markers_P.add(marker);
                }
            }

            @Override
            public void onError(@NonNull Throwable exception) {
                Toast.makeText(getContext(), "Αδυναμία Εκτέλεσης ... Ελέγξτε τη σύνδεσή σας", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRequest(Marker marker) {
        int id = ((ParkingSpot) marker.getTag()).getId();
        SupabaseManager.addRequest(id, new JavaResultCallback<>() {
            @Override
            public void onSuccess(String value) {
                Toast.makeText(getContext(), "Εστάλει Αίτημα Σύζευξης", Toast.LENGTH_SHORT).show();
                marker_M.remove();
                circle.remove();
            }

            @Override
            public void onError(@NonNull Throwable exception) {
                System.out.println(exception.getMessage());
                Toast.makeText(getContext(), "Αδυναμία Εκτέλεσης ... Ελέγξτε τη σύνδεσή σας", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /// popup μενου απο κατω που καλει απο τη βαση τις συντεταγμενες του marker
    /// που επελεξε ο χρηστης μαζι με την ημερομηνια/ωρα που επελεξε
    /// αν ο χρηστης δεν εχει επιλεξει ωρα τοτε καλει με την τοπικη ωρα του συστηματος
    private void bottomMapSearch(Marker marker) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText("Αναζήτηση παρκινγκ σε ακτίνα 1km από marker");
        actionButton.setText("Αναζήτηση");
        actionButton.setOnClickListener(v -> {
            bottomDialog.dismiss();
            checkParking(marker);
        });

        bottomDialog.setContentView(view);
        bottomDialog.show();
    }

    /// μενου για αιτημα συζευξης του χρηστη με το Marker(πρασινο) ποε επιλεξε
    private void bottomMapRequest(Marker marker) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText(marker.getTitle());
        actionButton.setText("Σύζευξη");
        actionButton.setOnClickListener(v -> {
            bottomDialog.dismiss();
            addRequest(marker);
        });

        bottomDialog.setContentView(view);
        bottomDialog.show();
    }
}
