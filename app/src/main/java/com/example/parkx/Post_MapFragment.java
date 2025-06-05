package com.example.parkx;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.concurrent.Executor;

import kotlinx.datetime.LocalDateTime;

public class Post_MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker marker_M = null;

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

        LatLng athens = new LatLng(40.629269, 22.947412);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(athens, 12));

        ///απομακρυνει το προηγουμενο Marker και φτιαχνει νεο με τις καινουργιες συντεταγμενες
        mMap.setOnMapClickListener(latLng -> {
            if (marker_M != null) {
                marker_M.remove();
            }
            marker_M = mMap.addMarker(new MarkerOptions().position(latLng)
                    .title("Σημείο έτοιμο για προσθήκη"));
            //.title("Σημείο" + latLng.latitude + " " + latLng.longitude));
        });

        mMap.setOnMarkerClickListener(marker -> {
            bottomMap(marker);
            return false;
        });

    }

    @SuppressLint("SetTextI18n")
    public void bottomMap(Marker marker) {
        ///Log.d("MapFragment", "Marker clicked: " + marker.getTitle());
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText(marker.getTitle());
        actionButton.setText("Προσθήκη Θέσης Πάρκινγκ");
        actionButton.setOnClickListener(v -> {
            bottomDialog.dismiss();
            LatLng temp = marker.getPosition();

            /// δεν ξερω αν λειτουργεί σωστά ως προς τη βάση , κοίτα LocalDateTime
            SupabaseManager.publishSpot(temp.latitude, temp.longitude, new LocalDateTime(java.time.LocalDateTime.now()).getValue$kotlinx_datetime(), new JavaResultCallback<String>() {
                @Override
                public void onSuccess(String value) {
                    Toast.makeText(getContext(), "Σωστή Εκτέλεση", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onError(@NonNull Throwable exception) {
                    Toast.makeText(getContext(), "Αδυναμία Εκτέλεσης ... Ελέγξτε τη σύνδεσή σας", Toast.LENGTH_SHORT).show();

                }
            });
            Toast.makeText(getContext(), "Προστέθηκε η Θέση Πάρκινκ",
                    Toast.LENGTH_SHORT).show();
        });
        bottomDialog.setContentView(view);
        bottomDialog.show();
    }
}
