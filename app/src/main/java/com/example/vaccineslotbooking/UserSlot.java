package com.example.vaccineslotbooking;

import java.util.HashMap;
import java.util.Map;

public class UserSlot {
    private String slotDate;
    private String slotLocation;
    private String vaccineName;
    private String slotTime;
    private Long availableSlots;
    private String id;

    public UserSlot(String slotDate, String slotLocation, String vaccineName, String slotTime, Long availableSlots, String id) {
        this.slotDate = slotDate;
        this.slotLocation = slotLocation;
        this.vaccineName = vaccineName;
        this.slotTime = slotTime;
        this.availableSlots = availableSlots;
        this.id = id;
    }

    public String getSlotDate() {
        return slotDate;
    }

    public String getSlotLocation() {
        return slotLocation;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public String getSlotTime() {
        return slotTime;
    }

    public Long getAvailableSlots() {
        return availableSlots;
    }

    public String getId() {
        return id;
    }

    // Create a map for slot details to store in Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> slotData = new HashMap<>();
        slotData.put("slotDate", slotDate);
        slotData.put("slotLocation", slotLocation);
        slotData.put("vaccineName", vaccineName);
        slotData.put("slotTime", slotTime);
        return slotData;
    }
}
