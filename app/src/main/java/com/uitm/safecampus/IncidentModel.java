package com.uitm.safecampus;

// This class follows the Lab 13 standard for Firebase Data Models
public class IncidentModel {
    private String userName;
    private String type;
    private String incidentType;
    private String description;
    private double latitude;
    private double longitude;
    private String timestamp;

    // 1. Empty Constructor (REQUIRED for Firebase to work!)
    public IncidentModel() {}

    // 2. Full Constructor to easily create a report
    public IncidentModel(String userName, String incidentType, String type, String description,
                         double latitude, double longitude, String timestamp) {
        this.userName = userName;
        this.type = type;
        this.incidentType = incidentType;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // 3. Getters and Setters
    // (Android Studio can generate these: Right-click > Generate > Getter and Setter)
    public String getUserName() { return userName; }
    public String getType() { return type; }
    public String getIncidentType() { return incidentType; }
    public String getDescription() { return description; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getTimestamp() { return timestamp; }
}