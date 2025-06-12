package com.example.parkx;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchMapFragment extends MapFragment {
    private final List<Marker> markers_P = new ArrayList<>();
    private Circle circle = null;
    private int minutes=0,hours=0,day=0,month=0,year=0;
    private LocalDateTime localDateTime;
    private Button button_date_time;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button_date_time=view.findViewById(R.id.btn_date);
        button_date_time.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                setTime();
            }
        });
    }

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

        /// ελεγχος αν εχουν μπει τιμες τοτε ορισε ημερομηνιες αλλιως συνεχισε με την τοπική ώρα
        if(year==0)
            localDateTime=LocalDateTime.now();
        else
            localDateTime = LocalDateTime.of(year,month,day,hours,minutes);
        Toast.makeText(getContext(), "Ημερομηνια : "+year+"-"+month+"-"+day+" "
                +hours+":"+minutes, Toast.LENGTH_SHORT).show();

        SupabaseManager.getSpots(temp.latitude, temp.longitude, localDateTime, new JavaResultCallback<>() {
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

        title.setText(marker.getTitle());
        actionButton.setText("Αναζήτηση Θέσης Πάρκινγκ");
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

    /// εμφανιση TimePicker για ημερομηνία και ώρα
    private void setTime(){

        /// προσθετω +3 στην ωρα για το 24ωρο ρολοι
        @SuppressLint("SetTextI18n") TimePickerDialog.OnTimeSetListener onTimeSetListener= (timePicker, selectedHour, selectedMinute) -> {
            hours=selectedHour+3;
            minutes=selectedMinute;
            button_date_time.setText(day+"-"+month+"-"+year+"  "+hours+":"+minutes+":00");
            Log.d("mytag", "Ώρα: " + selectedHour + ":" + selectedMinute);
        };

        TimePickerDialog timePickerDialog=new TimePickerDialog(getContext(),onTimeSetListener,hours,minutes,true);
        timePickerDialog.show();

        /// προσθετω +1 στο μηνα γιατι ξεκιναει απο το 0 (Ιανουαριος=0)
        DatePickerDialog onDateSetListener =new DatePickerDialog(requireContext(), (datePicker, Year, Month, dayOfMonth) -> {
            year=Year;
            month=Month+1;
            day=dayOfMonth;
            }, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue()-1, LocalDateTime.now().getDayOfMonth());
        onDateSetListener.show();

    }

}
