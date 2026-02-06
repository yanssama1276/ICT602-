package com.uitm.safecampus;

public class NewsItem {
    public String title, date, description, type, location;
    public double latitude, longitude; // New coordinate fields

    public NewsItem(String title, String date, String description, String type, String location, double latitude, double longitude) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.type = type;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}