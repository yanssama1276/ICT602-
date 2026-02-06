package com.uitm.safecampus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // 1. Initialize the Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // 2. Setup UI Elements
        Button btnSOS = view.findViewById(R.id.btnSOS);
        Button btnShare = view.findViewById(R.id.btnShare);
        ImageView btnMenu = view.findViewById(R.id.btnMenu);
        ImageView btnSearchIcon = view.findViewById(R.id.btnSearchIcon);
        TextView tvSearchText = view.findViewById(R.id.tvSearchText);

        // --- MENU LOGIC ---
        btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), btnMenu);
            popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_logout) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // --- SEARCH LOGIC ---
        View.OnClickListener searchAction = v -> {
            final EditText input = new EditText(requireContext());
            input.setHint("Try: Library, Cafe, Gate, Hostel");
            input.setPadding(50, 40, 50, 40);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Search Safe Zones")
                    .setView(input)
                    .setPositiveButton("Search", (dialog, which) -> {
                        String locationName = input.getText().toString().trim();
                        LatLng targetLocation = null;
                        String foundName = "";

                        if (locationName.equalsIgnoreCase("Library")) {
                            targetLocation = new LatLng(2.2230, 102.4540);
                            foundName = "Campus Library";
                        } else if (locationName.equalsIgnoreCase("Cafe")) {
                            targetLocation = new LatLng(2.2210, 102.4520);
                            foundName = "Student Cafe";
                        }

                        if (targetLocation != null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 18f));
                            mMap.addMarker(new MarkerOptions().position(targetLocation).title(foundName));
                        } else {
                            Toast.makeText(getActivity(), "Location not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        };
        tvSearchText.setOnClickListener(searchAction);
        btnSearchIcon.setOnClickListener(searchAction);

        // --- SOS BUTTON ---
        btnSOS.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:999"));
            startActivity(intent);
        });

        // --- SHARE LOCATION BUTTON (DYNAMIC) ---
        btnShare.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    String message;
                    if (location != null) {
                        message = "HELP! I need assistance. My location: https://www.google.com/maps?q="
                                + location.getLatitude() + "," + location.getLongitude();
                    } else {
                        message = "HELP! I need assistance. My location: UiTM Jasin Campus";
                    }
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(shareIntent, "Share Location via"));
                });
            } else {
                Toast.makeText(getActivity(), "Location permission required to share position.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();
    }

    private void updateLocationUI() {
        if (mMap == null) return;
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f));
                } else {
                    // Default to UiTM Jasin if location unavailable
                    LatLng uitmJasin = new LatLng(2.2216, 102.4533);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uitmJasin, 16f));
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocationUI();
                getDeviceLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }
}