package com.uitm.safecampus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportFragment extends Fragment {

    private TextView tvAutoLocation, tvAutoTime;
    private TextInputEditText etDescription;
    private Spinner spinnerType;
    private Button btnSendReport;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;
    private DatabaseReference rtdb;
    private com.google.android.gms.location.LocationCallback locationCallback;
    // Member variables to store numeric coordinates
    private double currentLat = 0.0;
    private double currentLng = 0.0;

    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private final Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (tvAutoTime != null) {
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                tvAutoTime.setText(currentTime);
            }
            timeHandler.postDelayed(this, 1000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_report, container, false);

        db = FirebaseFirestore.getInstance();
        rtdb = FirebaseDatabase.getInstance().getReference("reports");

        tvAutoLocation = view.findViewById(R.id.tvAutoLocation);
        tvAutoTime = view.findViewById(R.id.tvAutoTime);
        etDescription = view.findViewById(R.id.etDescription);
        spinnerType = view.findViewById(R.id.spinnerType);
        btnSendReport = view.findViewById(R.id.btnSendReport);

        ImageButton btnBack = view.findViewById(R.id.btnFloatingBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        captureLocation();

        btnSendReport.setOnClickListener(v -> {
            String description = etDescription.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String location = tvAutoLocation.getText().toString();
            String time = tvAutoTime.getText().toString();

            if (description.isEmpty()) {
                Toast.makeText(requireContext(), "Please describe the incident", Toast.LENGTH_SHORT).show();
            } else {
                Map<String, Object> report = new HashMap<>();
                report.put("type", type);
                report.put("description", description);
                report.put("location", location);
                report.put("time", time);
                // Include raw numeric coordinates for mapping/analysis
                report.put("latitude", currentLat);
                report.put("longitude", currentLng);

                db.collection("reports")
                        .add(report)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(requireContext(), "Report Submitted Successfully!", Toast.LENGTH_LONG).show();

                            // Use the Firestore document ID to keep Realtime Database in sync
                            String docId = documentReference.getId();
                            rtdb.child(docId).setValue(report)
                                    .addOnSuccessListener(aVoid -> Log.d("RealtimeDB", "Data synced successfully."))
                                    .addOnFailureListener(e -> Log.w("RealtimeDB", "Error syncing data.", e));

                            etDescription.setText("");
                            Intent intent = new Intent(getActivity(), DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Firestore", "Error adding document", e);
                            Toast.makeText(requireContext(), "Error submitting report.", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        timeHandler.post(timeRunnable);
        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timeHandler.removeCallbacks(timeRunnable);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }

    private void captureLocation() {
        com.google.android.gms.location.LocationRequest locationRequest =
                com.google.android.gms.location.LocationRequest.create()
                        .setPriority(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY)
                        .setInterval(5000) // Update every 5 seconds
                        .setFastestInterval(2000);

        locationCallback = new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(@NonNull com.google.android.gms.location.LocationResult locationResult) {
                for (android.location.Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();
                        updateAddressUI(currentLat, currentLng);
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    // Helper to keep code clean
    private void updateAddressUI(double lat, double lng) {
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                String coordString = String.format(Locale.getDefault(), "(%.4f, %.4f)", lat, lng);
                String finalAddress = (addresses != null && !addresses.isEmpty())
                        ? addresses.get(0).getAddressLine(0) + " " + coordString
                        : "Coordinates: " + coordString;

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> tvAutoLocation.setText(finalAddress));
                }
            } catch (IOException e) {
                Log.e("GEO", "Geocoder failed", e);
            }
        }).start();
    }
}