package com.example.vaccineslotbooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class VaccineAdapter extends ArrayAdapter<Vaccine> {
    private final int resource;
    private final List<Vaccine> vaccines;

    public VaccineAdapter(@NonNull Context context, int resource, @NonNull List<Vaccine> vaccines) {
        super(context, resource, vaccines);
        this.resource = resource;
        this.vaccines = vaccines;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }

        TextView vaccineNameTextView = convertView.findViewById(R.id.tv_vaccine_name);
        TextView dateTextView = convertView.findViewById(R.id.tv_vaccine_date);

        Vaccine vaccine = vaccines.get(position);

        vaccineNameTextView.setText("Vaccine: " + vaccine.getVaccineName());
        dateTextView.setText("Date: " + vaccine.getDate());

        return convertView;
    }
}
