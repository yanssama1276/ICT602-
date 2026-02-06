package com.uitm.safecampus;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsRepository {
    public static List<NewsItem> newsList = new ArrayList<>();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static DatabaseReference rtdb = FirebaseDatabase.getInstance().getReference("reports");

    public interface DataChangedListener {
        void onDataLoaded();
    }

    public static void startSyncing(DataChangedListener listener) {
        db.collection("reports")
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    if (value != null) {
                        newsList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            String type = doc.getString("type");
                            String time = doc.getString("time");
                            String desc = doc.getString("description");
                            String location = doc.getString("location");
                            double lat = doc.contains("latitude") ? doc.getDouble("latitude") : 0.0;
                            double lng = doc.contains("longitude") ? doc.getDouble("longitude") : 0.0;

                            // 1. Update UI List
                            newsList.add(new NewsItem(type, time, desc, type, location, lat, lng));

                            // 2. Mirror to Realtime Database using Firestore ID as key
                            syncToRTDB(doc.getId(), type, time, desc, location, lat, lng);
                        }
                        if (listener != null) listener.onDataLoaded();
                    }
                });
    }

    private static void syncToRTDB(String id, String type, String time, String desc, String loc, double lat, double lng) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("time", time);
        map.put("description", desc);
        map.put("location", loc);
        map.put("latitude", lat);
        map.put("longitude", lng);
        rtdb.child(id).setValue(map);
    }
}