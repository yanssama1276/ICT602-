package com.uitm.safecampus;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Load the modern layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Initialize the Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // 2. Setup Bottom Buttons (SOS & Share)
        Button btnSOS = view.findViewById(R.id.btnSOS);
        Button btnShare = view.findViewById(R.id.btnShare);

        // 3. Setup Top Bar Icons (Menu & Search)
        ImageView btnMenu = view.findViewById(R.id.btnMenu);
        ImageView btnSearchIcon = view.findViewById(R.id.btnSearchIcon);
        TextView tvSearchText = view.findViewById(R.id.tvSearchText);

        // --- FUNCTION 1: MENU ICON (Opens Popup Menu with Logout) ---
        btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), btnMenu);
            popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_logout) {
                    Toast.makeText(getActivity(), "Logging Out...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // --- FUNCTION 2: SEARCH (SMART VERSION - MOVES THE MAP) ---
        View.OnClickListener searchAction = v -> {
            final EditText input = new EditText(requireContext());
            input.setHint(" Try: Library, Cafe, Gate, Hostel");
            input.setPadding(50, 40, 50, 40);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Search Safe Zones")
                    .setMessage("Where do you want to go?")
                    .setView(input)
                    .setPositiveButton("Search", (dialog, which) -> {
                        String locationName = input.getText().toString().trim();

                        // --- TEACHING THE APP LOCATIONS ---
                        LatLng targetLocation = null;
                        String foundName = "";

                        // Check what the user typed (Case Insensitive)
                        if (locationName.equalsIgnoreCase("Library")) {
                            targetLocation = new LatLng(2.2230, 102.4540); // Fake coords for demo
                            foundName = "Campus Library";
                        }
                        else if (locationName.equalsIgnoreCase("Cafe")) {
                            targetLocation = new LatLng(2.2210, 102.4520);
                            foundName = "Student Cafe";
                        }
                        else if (locationName.equalsIgnoreCase("Gate")) {
                            targetLocation = new LatLng(2.2200, 102.4500);
                            foundName = "Main Entrance";
                        }
                        else if (locationName.equalsIgnoreCase("Hostel")) {
                            targetLocation = new LatLng(2.2250, 102.4560);
                            foundName = "Student Hostel";
                        }

                        // --- LOGIC: DID WE FIND IT? ---
                        if (targetLocation != null) {
                            // 1. Move Camera to the found spot
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 18f));

                            // 2. Place a Marker there
                            mMap.addMarker(new MarkerOptions().position(targetLocation).title(foundName));

                            // 3. Tell the user
                            Toast.makeText(getActivity(), "Found: " + foundName, Toast.LENGTH_SHORT).show();
                        } else {
                            // Location not in our list
                            Toast.makeText(getActivity(), "Location not found. Try 'Library' or 'Cafe'", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        };

        // Attach the search action to both text and icon
        tvSearchText.setOnClickListener(searchAction);
        btnSearchIcon.setOnClickListener(searchAction);

        // --- FUNCTION 3: SOS BUTTON ---
        btnSOS.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:999"));
            startActivity(intent);
        });

        // --- FUNCTION 4: SHARE BUTTON ---
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String message = "HELP! I need assistance. My current location is: UiTM Jasin Campus (2.2216, 102.4533)";
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, "Share Location via"));
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add marker for UiTM Jasin (Start Point)
        LatLng uitmJasin = new LatLng(2.2216, 102.4533);
        mMap.addMarker(new MarkerOptions().position(uitmJasin).title("UiTM Jasin Campus"));

        // Move camera and zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uitmJasin, 16f));
    }

    // --- HIDE THE BLUE TITLE BAR ONLY ON HOME PAGE ---
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