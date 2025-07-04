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

    // This method is called when the fragment's view is created. It restores the visibility state of the bottom map dialog for search or request and the ID of the request, the circle center and parking spots from the saved instance state.
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            bottomMapSearchVisible = savedInstanceState.getBoolean("bottomMapSearchVisible", false);
            bottomMapRequestVisible = savedInstanceState.getBoolean("bottomMapRequestVisible", false);
            requestId = savedInstanceState.getInt("requestId", -1);
            circleCenter = savedInstanceState.getParcelable("circleCenter");
            parkingSpots = (List<ParkingSpot>) savedInstanceState.getSerializable("parkingSpots");
        }
    }

    // This method is called when the map is ready to be used.
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        super.onMapReady(googleMap);
        // Sets a long click listener that places a marker that will be used for search
        // and removes the previous marker and circle if they exist.
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

        // Sets a marker click listener that activates the bottomMapSearch method if the marker is the search marker
        // or the bottomMapRequest method if the marker is a parking spot marker.
        mMap.setOnMarkerClickListener(marker -> {
            if (marker.equals(marker_M)) bottomMapSearch(marker);
            else if (markers_P.contains(marker)) bottomMapRequest(marker);
            return false;
        });

        //If a bottom map search dialog was previously visible, it reopens it with the search marker.
        if (bottomMapSearchVisible && marker_M != null) {
            bottomMapSearch(marker_M);
        }
        //If a circle is saved it reloads it
        if (circleCenter != null) {
            circle = mMap.addCircle(circleOptions.center(circleCenter));
        }
        //It creates a marker for each parking spot and adds it
        for (ParkingSpot p : parkingSpots) {
            markers_P.add(makeMarker(p));
            if (p.getId() == requestId)
                // If there was an ongoing request, it reopens the bottom map request dialog for that marker
                bottomMapRequest(markers_P.get(markers_P.size() - 1));
        }
    }


    // This method is called to add a spot inside the circle as a green marker
    private Marker makeMarker(ParkingSpot parkingSpot) {
        LatLng ParkingLocationXY = new LatLng(parkingSpot.getLatitude(), parkingSpot.getLongitude());
        MarkerOptions ParkingMarker = new MarkerOptions().position(ParkingLocationXY).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        Marker marker = mMap.addMarker(ParkingMarker);
        assert marker != null;
        marker.setTag(parkingSpot);
        return marker;
    }


    //   This method calls the SupabaseManager method that checks if there are parking spots from other users within a 1000 m radius from the search marker
    //   for the approximate dateTime provided and if the database method is successful it
    //   draws the parking spot markers  and removes the circle.
    //   If the database method is not successful it provides an error message.
    private void checkParking(Marker marker) {
        LatLng temp = marker.getPosition();

        markers_P.clear();
        SupabaseManager.getNearbySpots(temp.latitude, temp.longitude, dateTime, new JavaResultCallback<>() {
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
                Toast.makeText(getContext(), "Something went wrong ..." + " Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // This method calls the SupabaseManager method addRequest with argument the id of the parking spot.
    // It creates a request that will be sent to the owner of the spot.
    private void addRequest(Marker marker) {
        int id = ((ParkingSpot) marker.getTag()).getId();
        SupabaseManager.addRequest(id, new JavaResultCallback<>() {
            @Override
            public void onSuccess(String value) {
                Toast.makeText(getContext(), "Request sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Throwable exception) {
                System.out.println(exception.getMessage());
                Toast.makeText(getContext(), "Something went wrong ... " + "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //A pop up menu with a button that a user presses to search for parking spots using the checkParking method
    private void bottomMapSearch(Marker marker) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText(R.string.searching_parking_spot);
        actionButton.setText(R.string.search);
        actionButton.setOnClickListener(v -> {
            bottomDialog.cancel();
            checkParking(marker);
        });

        bottomDialog.setOnCancelListener(dialog -> bottomMapSearchVisible = false);
        bottomDialog.setContentView(view);
        bottomDialog.getBehavior().setPeekHeight(1000);
        bottomDialog.show();
        bottomMapSearchVisible = true;
    }

    //A pop up menu with a button that a user presses to create a request using the addRequest method
    private void bottomMapRequest(Marker marker) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        ParkingSpot ps = (ParkingSpot) marker.getTag();

        title.setText("(" + ps.getLatitude() + ", " + ps.getLongitude() + ")" + "\n" + ps.getExchangeTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        actionButton.setText(R.string.make_a_request);
        actionButton.setOnClickListener(v -> {
            bottomDialog.cancel();
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

    // This method saves the visibility state of the bottom sheet dialogs for search and request, the request ID, the circle center, and the list of parking spots when the fragment is paused or stopped.
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
