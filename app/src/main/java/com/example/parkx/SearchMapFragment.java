package com.example.parkx;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
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
    private final CircleOptions circleOptions = new CircleOptions().radius(1000).strokeColor(Color.CYAN).fillColor(0x220000FF).strokeWidth(3);
    private final List<Marker> markers_P = new ArrayList<>();
    private List<ParkingSpot> parkingSpots = new ArrayList<>();
    private boolean bottomMapSearchVisible = false;
    private boolean bottomMapRequestVisible = false;
    private int requestId = -1;
    private Circle circle = null;
    private LatLng circleCenter;

    /**
     * @param view               The View returned by @link #onCreateView(LayoutInflater, ViewGroup, Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            bottomMapSearchVisible = savedInstanceState.getBoolean("bottomMapSearchVisible", false);
            bottomMapRequestVisible = savedInstanceState.getBoolean("bottomMapRequestVisible", false);
            circleCenter = savedInstanceState.getParcelable("circleCenter");
            parkingSpots = (List<ParkingSpot>) savedInstanceState.getSerializable("parkingSpots");
            requestId = savedInstanceState.getInt("requestId", -1);
        }
    }

    /**
     * @param googleMap προετοιμασια χαρτη ορισμος marker και circle
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        super.onMapReady(googleMap);

        mMap.setOnMapLongClickListener(latLng -> {
            if (marker_M != null) marker_M.remove();
            if (circle != null) circle.remove();
            circle = null;
            for (Marker marker : markers_P) {
                marker.remove();
            }
            markers_P.clear();
            parkingSpots.clear();

            marker_M = mMap.addMarker(new MarkerOptions().position(latLng));
        });

        mMap.setOnMarkerClickListener(marker -> {
            if (marker.equals(marker_M)) bottomMapSearch(marker);
            else if (markers_P.contains(marker)) bottomMapRequest(marker);
            return false;
        });

        if (bottomMapSearchVisible && marker_M != null) {
            bottomMapSearch(marker_M);
        }
        if (circleCenter != null) {
            circle = mMap.addCircle(circleOptions.center(circleCenter));
        }
        for (ParkingSpot p : parkingSpots) {
            markers_P.add(makeMarker(p));
        }
        if (bottomMapRequestVisible && requestId != -1) {
            for (Marker marker : markers_P) {
                if (((ParkingSpot) marker.getTag()).getId() == requestId) {
                    bottomMapRequest(marker);
                    break;
                }
            }
        }
    }

    /**
     * @param parkingSpot , μεθοδος που καλειτε για να προσθεσει το σημειο εντος κυκλου ως πρασινο marker
     * @return marker
     */
    private Marker makeMarker(ParkingSpot parkingSpot) {
        LatLng ParkingLocationXY = new LatLng(parkingSpot.getLatitude(), parkingSpot.getLongitude());
        MarkerOptions ParkingMarker = new MarkerOptions().position(ParkingLocationXY)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        Marker marker = mMap.addMarker(ParkingMarker);
        assert marker != null;
        marker.setTag(parkingSpot);
        return marker;
    }

    /**
     * @param marker μεθοδος ελεγχου παρκινγ που καλει απο τη βαση τις συντεταγμενες ολων των marker
     *               που ειναι σε ακτινα 1000μ απο το σημειο του χρηστη (Marker) μαζι με την ωρα επιλογης,
     *               αλλιως στελνει με ωρα συστηματος για επιτυχη αναζητηση εφμανιζεται ενας κυκλος
     *               ακτινας 1km γυρο απο το marker και μεσα σε αυτο
     */
    private void checkParking(Marker marker) {
        LatLng temp = marker.getPosition();

        SupabaseManager.getSpots(temp.latitude, temp.longitude, dateTime, new JavaResultCallback<>() {
            @Override
            public void onSuccess(List<ParkingSpot> value) {
                parkingSpots = value;
                if (circle != null) circle.remove();
                circle = mMap.addCircle(circleOptions.center(temp));
                for (ParkingSpot p : value) {
                    markers_P.add(makeMarker(p));
                }
            }

            @Override
            public void onError(@NonNull Throwable exception) {
                Toast.makeText(getContext(), "Αδυναμία Εκτέλεσης ..." +
                        " Ελέγξτε τη σύνδεσή σας", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @param marker , παίρνει τις συντεταγμενες απο το marker και καλεί τη βάση δεδομένων
     *               για να προσθεσει αιτημα αναζητησης με τις συντεταγμενες του marker
     */
    private void addRequest(Marker marker) {
        int id = ((ParkingSpot) marker.getTag()).getId();
        SupabaseManager.addRequest(id, new JavaResultCallback<>() {
            @Override
            public void onSuccess(String value) {
                Toast.makeText(getContext(), "Εστάλει αίτημα", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Throwable exception) {
                System.out.println(exception.getMessage());
                Toast.makeText(getContext(), "Αδυναμία Εκτέλεσης ... " +
                        "Ελέγξτε τη σύνδεσή σας", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @param marker popup μενου απο κατω που καλει απο τη βαση τις συντεταγμενες του marker
     *               που επελεξε ο χρηστης μαζι με την ημερομηνια/ωρα που επελεξε
     *               αν ο χρηστης δεν εχει επιλεξει ωρα τοτε καλει με την τοπικη ωρα του συστηματος
     */
    private void bottomMapSearch(Marker marker) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText("Αναζήτηση παρκινγκ σε ακτίνα 1km από marker");
        actionButton.setText("Αναζήτηση");
        actionButton.setOnClickListener(v -> {
            bottomDialog.cancel();
            checkParking(marker);
        });

        bottomDialog.setOnCancelListener(dialog -> {
            bottomMapSearchVisible = false;
        });
        bottomDialog.setContentView(view);
        bottomDialog.getBehavior().setPeekHeight(1000);
        bottomDialog.show();
        bottomMapSearchVisible = true;
    }

    /**
     * @param marker
     */
    private void bottomMapRequest(Marker marker) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).
                inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText(marker.getTitle());
        actionButton.setText("Κάνε αίτημα");
        actionButton.setOnClickListener(v -> {
            bottomDialog.dismiss();
            addRequest(marker);
        });

        bottomDialog.setOnCancelListener(dialog -> {
            bottomMapRequestVisible = false;
            requestId = -1;
        });
        bottomDialog.setContentView(view);
        bottomDialog.getBehavior().setPeekHeight(1000);
        bottomDialog.show();
        bottomMapRequestVisible = true;
        requestId = ((ParkingSpot) marker.getTag()).getId();
    }

    /**
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("bottomMapSearchVisible", bottomMapSearchVisible);
        outState.putBoolean("bottomMapRequestVisible", bottomMapRequestVisible);
        outState.putInt("requestId", requestId);
        if (circle != null) {
            outState.putParcelable("circleCenter", circle.getCenter());
        }
        outState.putSerializable("parkingSpots", new ArrayList<>(parkingSpots));
    }
}
