package com.uitm.safecampus;

public class Incident {
    public String type, description, details;

    public Incident(String type, String description, String details) {
        this.type = type;
        this.description = description;
        this.details = details;
    }
}