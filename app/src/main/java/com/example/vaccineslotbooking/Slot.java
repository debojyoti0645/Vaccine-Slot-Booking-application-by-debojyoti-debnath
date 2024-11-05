package com.example.vaccineslotbooking;

public class Slot {
    private String id;
    private String date;
    private String time;
    private String location;
    private String vaccineName;
    private int dosageNumber;
    private String status;

    // Default constructor required for Firestore
    public Slot() {
    }

    public Slot(String date, String time, String location, String vaccineName, int dosageNumber, String status) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.vaccineName = vaccineName;
        this.dosageNumber = dosageNumber;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public int getDosageNumber() {
        return dosageNumber;
    }

    public String getStatus() {
        return status;
    }
}
