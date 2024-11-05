package com.example.vaccineslotbooking;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VaccinesTakenActivity extends AppCompatActivity {
    private ListView vaccineListView;
    private FirebaseFirestore db;
    private VaccineAdapter vaccineAdapter;
    private List<Vaccine> vaccineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccines_taken);

        vaccineListView = findViewById(R.id.vaccineListView);
        db = FirebaseFirestore.getInstance();
        vaccineList = new ArrayList<>();
        vaccineAdapter = new VaccineAdapter(this, R.layout.list_item_vaccine, vaccineList);
        vaccineListView.setAdapter(vaccineAdapter);

        loadVaccinesTaken();

        vaccineListView.setOnItemClickListener((parent, view, position, id) -> {
            Vaccine selectedVaccine = vaccineList.get(position);
            showVaccineDetailsDialog(selectedVaccine);
        });
    }

    private void loadVaccinesTaken() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).collection("bookedVaccines")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        vaccineList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String vaccineName = document.getString("vaccineName");
                            String date = document.getString("slotDate");
                            String location = document.getString("slotLocation");

                            vaccineList.add(new Vaccine(vaccineName, date, location));
                        }
                        vaccineAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load vaccines", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showVaccineDetailsDialog(Vaccine vaccine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vaccine Details")
                .setMessage("Vaccine Name: " + vaccine.getVaccineName() +
                        "\nDate of vaccination: " + vaccine.getDate() +
                        "\nLocation: " + vaccine.getLocation())
                .setPositiveButton("OK", null)
                .show();
    }
}
