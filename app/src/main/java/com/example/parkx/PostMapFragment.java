package com.example.parkx;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
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

import java.time.LocalDateTime;

public class PostMapFragment extends MapFragment {

    private int minutes = 0, hours = 0, day = 0, month = 0, year = 0;
    private LocalDateTime localDateTime = LocalDateTime.now();
    private Button button_date_time;

    /**
     * @param view               The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     *                           καλειται η συνατηση setTime() για να οριστουν ημερομηνια και ωρα
     *                           στο κουμπι button_date_time
     */
    @SuppressLint("CutPasteId")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button_date_time = view.findViewById(R.id.btn_date);
        button_date_time.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                setTime();
            }
        });
    }

    /**
     * @param googleMap προετοιμασια χαρτη, μεθοδος που απομακρυνει το προηγουμενο Marker,
     *                  φτιαχνει νεο με τις καινουργιες συντεταγμενες και
     *                  ορισμος μεθοδου για popup menu με χρηση συναρτησης bottomMap
     *
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        super.onMapReady(googleMap);

        //
        mMap.setOnMapLongClickListener(latLng -> {
            if (marker_M != null) {
                marker_M.remove();
            }
            marker_M = mMap.addMarker(new MarkerOptions().position(latLng));
        });

        mMap.setOnMarkerClickListener(marker -> {
            bottomMap(marker);
            return false;
        });
    }

    /**
     * μεθοδος popup μενου απο κατω που στελνει στη βαση τις συντεταγμενες του marker
     * που επελεξε ο χρηστης μαζι με την ημερομηνια/ωρα που επελεξε
     * αν ο χρηστης δεν εχει επιλεξει ωρα τοτε καλει με την τοπικη ωρα του συστηματος
     */
    public void bottomMap(Marker marker) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_map, null);

        TextView title = view.findViewById(R.id.textView_MAP);
        Button actionButton = view.findViewById(R.id.button_MAP);

        title.setText("Θέλετε να προσθέσετε αυτή τη θέση πάρκινγκ?");
        actionButton.setText("Προσθήκη Θέσης Παρκινγκ");
        actionButton.setOnClickListener(v -> {
            bottomDialog.dismiss();
            LatLng temp = marker.getPosition();

            // ελεγχος αν εχουν μπει τιμες τοτε ορισε ημερομηνιες αλλιως συνεχισε με την τοπική ώρα
            if (year == 0)
                localDateTime = LocalDateTime.now();
            else
                localDateTime = LocalDateTime.of(year, month, day, hours, minutes);
            Toast.makeText(getContext(), "Ημερομηνια : " + year + "-" + month + "-" + day + " "
                    + (hours - 3) + ":" + minutes, Toast.LENGTH_SHORT).show();

            SupabaseManager.publishSpot(temp.latitude, temp.longitude, localDateTime, new JavaResultCallback<>() {
                @Override
                public void onSuccess(String value) {
                    Toast.makeText(getContext(), "Σωστή Εκτέλεση", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull Throwable exception) {
                    Toast.makeText(getContext(), "Αδυναμία Εκτέλεσης ... Ελέγξτε τη σύνδεσή σας", Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(getContext(), "Προστέθηκε η Θέση Πάρκινκ", Toast.LENGTH_SHORT).show();
        });
        bottomDialog.setContentView(view);
        bottomDialog.show();
    }

    /**
     *
     * Αυτή η μέθοδος όριζει την ώρα και την ημερομηνια στις τοπικά στις αντιστοιχες μεταβλητές
     * year,month,day και hours,minutes
     */
    private void setTime() {

        // προσθετω +3 στην ωρα για το 24ωρο ρολοι
        @SuppressLint("SetTextI18n") TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, selectedHour, selectedMinute) -> {
            hours = selectedHour + 3;
            minutes = selectedMinute;
            button_date_time.setText(day + "-" + month + "-" + year + "  " + hours + ":" + minutes + ":00");
            Log.d("mytag", "Ώρα: " + selectedHour + ":" + selectedMinute);
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hours, minutes, true);
        timePickerDialog.show();

        // προσθετω +1 στο μηνα γιατι ξεκιναει απο το 0 (Ιανουαριος=0)
        @SuppressLint("SetTextI18n") DatePickerDialog onDateSetListener = new DatePickerDialog(requireContext(), (datePicker, Year, Month, dayOfMonth) -> {
            year = Year;
            month = Month + 1;
            day = dayOfMonth;
        }, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue() - 1, LocalDateTime.now().getDayOfMonth());
        onDateSetListener.show();

    }
}
