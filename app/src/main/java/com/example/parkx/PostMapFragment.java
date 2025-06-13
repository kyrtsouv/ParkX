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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null)
            bottomMapPostVisible = savedInstanceState.getBoolean("bottomMapPostVisible", false);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        super.onMapReady(googleMap);

        ///απομακρυνει το προηγουμενο Marker και φτιαχνει νεο με τις καινουργιες συντεταγμενες
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

    /// μεθοδος popup μενου απο κατω που στελνει στη βαση τις συντεταγμενες του marker
    /// που επελεξε ο χρηστης μαζι με την ημερομηνια/ωρα που επελεξε
    /// αν ο χρηστης δεν εχει επιλεξει ωρα τοτε καλει με την τοπικη ωρα του συστηματος
    public void bottomMapPost(Marker marker) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText("Θέλετε να προσθέσετε αυτή τη θέση πάρκινγκ?");
        actionButton.setText("Προσθήκη Θέσης Παρκινγκ");
        actionButton.setOnClickListener(v -> {
            bottomDialog.cancel();
            LatLng temp = marker.getPosition();

            SupabaseManager.publishSpot(temp.latitude, temp.longitude, dateTime, new JavaResultCallback<>() {
                @Override
                public void onSuccess(String value) {
                    Toast.makeText(getContext(), "Προστέθηκε η Θέση Πάρκινκ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull Throwable exception) {
                    Toast.makeText(getContext(), "Αδυναμία Εκτέλεσης ... Ελέγξτε τη σύνδεσή σας", Toast.LENGTH_SHORT).show();
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
