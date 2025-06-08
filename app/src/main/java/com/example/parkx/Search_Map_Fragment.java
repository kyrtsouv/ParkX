package com.example.parkx;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.example.parkx.utils.ParkingSpot;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import kotlinx.datetime.LocalDateTime;

public class Search_Map_Fragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ImageButton btnSettings;
    private Marker marker_M = null, marker_P = null;
    private Circle circle = null;
    private int RADIUS = 1000;//ακτινα κυκλου γύρο από το χρηστη

    public Search_Map_Fragment() {
        // Required empty public constructor
    }

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

        btnSettings = view.findViewById(R.id.btnSettings);
        //btnSettings.setVisibility(View.VISIBLE);
        btnSettings.setOnClickListener(v -> {
            Log.d("mytag", "btn is pressed");
            showDialog();
        });

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng athens = new LatLng(40.629269, 22.947412);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(athens, 12));

        ///απομακρυνει το προηγουμενο Marker και φτιαχνει νεο με τις καινουργιες συντεταγμενες
        mMap.setOnMapClickListener(latLng -> {
            if (marker_M != null)
                marker_M.remove();

            if (circle != null)
                circle.remove();
            marker_M = mMap.addMarker(new MarkerOptions().position(latLng)
                    .title("Σημείο προς Αναζήτηση?"));
        });

        mMap.setOnMarkerClickListener(marker -> {
            if (marker.equals(marker_M))
                bottomMapSearch(marker);
            else if (marker.equals(marker_P))
                bottomMapParking(marker);
            return false;
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().isRotateGesturesEnabled();
    }

    private void checkParking(Marker marker) {
        LatLng temp = marker.getPosition();

        circle = mMap.addCircle(new CircleOptions().center(temp).radius(RADIUS)
                .strokeColor(Color.CYAN).fillColor(0x220000FF).strokeWidth(3));

        /// λειτουργεί σωστά ως προς τη βάση
        SupabaseManager.getSpots(temp.latitude, temp.longitude, new LocalDateTime(java.time.LocalDateTime.now()).getValue$kotlinx_datetime(), new JavaResultCallback<List<ParkingSpot>>() {
            @Override
            public void onSuccess(List<ParkingSpot> value) {
                for (ParkingSpot p : value) {
                    LatLng ParkingLocationXY = new LatLng(p.getLatitude(), p.getLongitude());
                    MarkerOptions ParkingMarker = new MarkerOptions().position(ParkingLocationXY)
                            .title("Ελεύθερη Θέση Πάρκινγκ")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    marker_P = mMap.addMarker(ParkingMarker);
                    double distance = DistanceBetween(marker.getPosition(), ParkingMarker.getPosition());
                    assert marker_P != null;
                    marker_P.setVisible(distance < RADIUS);
                }
            }

            @Override
            public void onError(@NonNull Throwable exception) {
                Toast.makeText(getContext(), "Αδυναμία Εκτέλεσης ... Ελέγξτε τη σύνδεσή σας", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
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

    @SuppressLint("SetTextI18n")
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

    public static double DistanceBetween(LatLng point1, LatLng point2) {
        final int R = 6371000; // ακτίνα της Γης σε μέτρα

        double lat1 = Math.toRadians(point1.latitude);
        double lon1 = Math.toRadians(point1.longitude);
        double lat2 = Math.toRadians(point2.latitude);
        double lon2 = Math.toRadians(point2.longitude);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // επιστρέφει την απόσταση σε μέτρα
    }

    @SuppressLint("SetTextI18n")
    public void showDialog() {
        final Dialog dialog = new Dialog(requireContext());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);

        dialog.setContentView(R.layout.dialog_radius);

        final EditText number = dialog.findViewById(R.id.inputNumber);
        final Button btn = dialog.findViewById(R.id.OK);

        btn.setOnClickListener(view -> {
            Log.d("mytag", "btn is pressed");
            try {
                RADIUS = Integer.parseInt(number.getText().toString());
                Toast.makeText(requireContext(), "Αλλαγή ακτίνας σε " + number
                        .getText().toString() + "m", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException ignored) {
            }

            dialog.dismiss();
        });

        dialog.show();

    }
}
