package com.example.vaccineslotbooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class UserSlotAdapter extends ArrayAdapter<UserSlot> {

    private final Context context;
    private final int resource;
    private final List<UserSlot> userSlotList;

    public UserSlotAdapter(@NonNull Context context, int resource, @NonNull List<UserSlot> userSlotList) {
        super(context, resource, userSlotList);
        this.context = context;
        this.resource = resource;
        this.userSlotList = userSlotList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView slotDate = convertView.findViewById(R.id.tv_slot_date);
        TextView slotLocation = convertView.findViewById(R.id.tv_location);
        TextView vaccineName = convertView.findViewById(R.id.tv_vaccine_name);
        TextView slotTime = convertView.findViewById(R.id.tv_vaccine_time);
        TextView slotAvailability = convertView.findViewById(R.id.tv_available_slots);
        Button bookButton = convertView.findViewById(R.id.btn_book_slot);

        UserSlot userSlot = userSlotList.get(position);

        // Set the slot details
        slotDate.setText("Date: " + userSlot.getSlotDate());
        slotLocation.setText("Location: " + userSlot.getSlotLocation());
        vaccineName.setText("Vaccine: " + userSlot.getVaccineName());
        slotTime.setText("Time: " + userSlot.getSlotTime());
        slotAvailability.setText("Availability: " + userSlot.getAvailableSlots());

        // Add listener to the "Book" button
        bookButton.setOnClickListener(view -> {
            if (context instanceof ViewAndBookSlotsActivity) {
                ((ViewAndBookSlotsActivity) context).showConfirmationDialog(userSlot);
            }
        });

        return convertView;
    }
}
