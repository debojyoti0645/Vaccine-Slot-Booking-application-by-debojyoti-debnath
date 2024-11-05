package com.example.vaccineslotbooking;

public class Certificate {
    private String vaccineName;
    private String slotDate;
    private String slotLocation;

    public Certificate(String vaccineName, String slotDate, String slotLocation) {
        this.vaccineName = vaccineName;
        this.slotDate = slotDate;
        this.slotLocation = slotLocation;
    }

    public String getVaccineName() { return vaccineName; }
    public String getSlotDate() { return slotDate; }
    public String getSlotLocation() { return slotLocation; }
}
