package com.uitm.safecampus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton; // Import for the new back button
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Inflate the new modern layout
        View view = inflater.inflate(R.layout.activity_about, container, false);

        // 2. Logic for "Floating Back Button" -> GO TO DASHBOARD (FIXED)
        ImageButton btnBack = view.findViewById(R.id.btnFloatingBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                // FIX: We use CLEAR_TASK and NEW_TASK.
                // This wipes the current screen history and forces the Dashboard to start fresh.
                // It ensures the button always works, even if the app thinks it's already open.
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        // 3. Logic for "Visit GitHub" Button
        Button btnGithub = view.findViewById(R.id.btnGithub);
        if (btnGithub != null) {
            btnGithub.setOnClickListener(v -> {
                Toast.makeText(getActivity(), "Opening Browser...", Toast.LENGTH_SHORT).show();
                // Replace with your actual GitHub link
                String url = "https://github.com/YourUsername/SafeCampus";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            });
        }

        return view;
    }

    // --- MODERN UI LOGIC: HIDE TOP BAR ---

    @Override
    public void onResume() {
        super.onResume();
        // Hide the purple "SafeCampus Dashboard" bar when this page opens
        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Bring the bar back when leaving this page (so other pages look normal)
        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }
}