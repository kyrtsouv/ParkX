package com.example.parkx;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    protected GoogleMap mMap;
    protected Marker marker_M;
    protected LatLng markerPosition;

    private ToggleFullscreenListener toggleFullscreenListener;

    private CameraPosition cameraPosition;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            toggleFullscreenListener = (ToggleFullscreenListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement ToggleFullscreenListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        if (savedInstanceState != null) {
            cameraPosition = savedInstanceState.getParcelable("cameraPosition");
            markerPosition = savedInstanceState.getParcelable("markerPosition");
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        ///αρχικοποιηση του Marker
        if (markerPosition != null) {
            marker_M = mMap.addMarker(new MarkerOptions().position(markerPosition));
        }

        LatLng thessaloniki = new LatLng(40.629269, 22.947412);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(thessaloniki, 12));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        if (cameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        mMap.setOnMapClickListener(latLng -> {
            toggleFullscreenListener.onToggleFullscreen();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMap != null) {
            outState.putParcelable("cameraPosition", mMap.getCameraPosition());
        }
        if (marker_M != null) {
            outState.putParcelable("markerPosition", marker_M.getPosition());
        }
    }

    public interface ToggleFullscreenListener {
        void onToggleFullscreen();
    }
}