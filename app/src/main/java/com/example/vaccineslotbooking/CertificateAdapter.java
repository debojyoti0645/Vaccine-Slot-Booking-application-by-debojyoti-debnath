package com.example.vaccineslotbooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CertificateAdapter extends ArrayAdapter<Certificate> {

    public CertificateAdapter(Context context, List<Certificate> certificateList) {
        super(context, 0, certificateList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_certificate, parent, false);
        }

        Certificate certificate = getItem(position);

        TextView vaccineName = convertView.findViewById(R.id.vaccineName);
        TextView vaccineDate = convertView.findViewById(R.id.vaccineDate);
        Button downloadButton = convertView.findViewById(R.id.downloadCertificateButton);

        vaccineName.setText(certificate.getVaccineName());
        vaccineDate.setText(certificate.getSlotDate());

        downloadButton.setOnClickListener(v -> {
            // Cast the context to DownloadCertificateActivity to access showDownloadDialog
            DownloadCertificateActivity activity = (DownloadCertificateActivity) getContext();
            activity.showDownloadDialog(certificate);
        });

        return convertView;
    }
}
