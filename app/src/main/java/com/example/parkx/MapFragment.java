package com.example.parkx;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

import java.time.LocalDateTime;

// Τhis class is a fragment that displays a Google Map and allows the user to interact with it.
// It is a base class for PostMapFragment and SearchMapFragment providing common functionality
// such as handling the map's camera position, marker placement, and date/time selection between configuration changes.
public class MapFragment extends Fragment implements OnMapReadyCallback {

    protected GoogleMap mMap;
    protected Marker marker_M;
    protected LatLng markerPosition;
    protected int year, month, day, hours, minutes;
    protected LocalDateTime dateTime = LocalDateTime.now();
    private ToggleFullscreenListener toggleFullscreenListener;
    private CameraPosition cameraPosition;
    private Button button_date_time;

    //Checks if toggleFullscreenListener is implemented
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

    // This method is called when the fragment's view is created.
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // Loads the map
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        // Reloads saved camera position, marker position and selected date and time  if they exist
        if (savedInstanceState != null) {
            cameraPosition = savedInstanceState.getParcelable("cameraPosition");
            markerPosition = savedInstanceState.getParcelable("markerPosition");
            dateTime = (LocalDateTime) savedInstanceState.getSerializable("dateTime");
        }

        // It initializes the button for selecting date and time, sets its text to the current date and time, and sets an onClickListener to open a date and time picker dialog.
        button_date_time = view.findViewById(R.id.btn_date);
        button_date_time.setText(dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        button_date_time.setOnClickListener(view1 -> setTime());
    }

    // This method is called when the map is ready to be used. It initializes the map, sets the initial camera position
    // and marker position if they were previously saved, and sets up the map's UI settings.
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //When the map is ready it focuses on Thessaloniki
        mMap = googleMap;

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
        //if the map is clicked it toggles fullscreen
        mMap.setOnMapClickListener(latLng -> toggleFullscreenListener.toggleFullscreen());
    }

    // This method is called when the fragment is paused or stopped, and it saves the current state of the map, marker, and selected date and time.
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mMap != null) {
            //saves the current camera position
            outState.putParcelable("cameraPosition", mMap.getCameraPosition());
        }
        if (marker_M != null) {
            //saves the position of the marker
            outState.putParcelable("markerPosition", marker_M.getPosition());
        }
        //saves the date and time selected
        outState.putSerializable("dateTime", dateTime);
    }

    // This method gets the hour and date from time and date picker assigns them to a local date time and is set as text to the button_date_time
    private void setTime() {
        new DatePickerDialog(requireContext(), (datePicker, selectedYear, selectedMonth, selectedDay) -> {
            year = selectedYear;
            month = selectedMonth + 1; // adds 1 because date picker starts at 0 for month
            day = selectedDay;

            new TimePickerDialog(getContext(), (timePicker, selectedHour, selectedMinute) -> {
                hours = selectedHour;
                minutes = selectedMinute;

                dateTime = LocalDateTime.of(year, month, day, hours, minutes);
                button_date_time.setText(dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

            }, hours, minutes, true).show();
        }, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue() - 1, LocalDateTime.now().getDayOfMonth()).show();

    }

    public interface ToggleFullscreenListener {
        void toggleFullscreen();
    }
}