package com.example.vaccineslotbooking;

public class Vaccine {
    private String vaccineName;
    private String slotDate;
    private String slotLocation;

    public Vaccine(String vaccineName, String slotDate, String slotLocation) {
        this.vaccineName = vaccineName;
        this.slotDate = slotDate;
        this.slotLocation = slotLocation;
    }

    public String getVaccineName() { return vaccineName; }
    public String getDate() { return slotDate; }
    public String getLocation() { return slotLocation; }
}