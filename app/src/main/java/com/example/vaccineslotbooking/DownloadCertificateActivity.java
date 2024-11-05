package com.example.vaccineslotbooking;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DownloadCertificateActivity extends AppCompatActivity {

    private ListView listView;
    private List<Certificate> certificateList;
    private CertificateAdapter adapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_certificate);

        listView = findViewById(R.id.certificateListView);

        certificateList = new ArrayList<>();
        adapter = new CertificateAdapter(this, certificateList);
        listView.setAdapter((ListAdapter) adapter);

        firestore = FirebaseFirestore.getInstance();
        loadCertificates();
    }

    private void loadCertificates() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore.collection("users").document(userId).collection("bookedVaccines")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    certificateList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String vaccineName = document.getString("vaccineName");
                        String slotDate = document.getString("slotDate");
                        String slotLocation = document.getString("slotLocation");

                        Certificate certificate = new Certificate(vaccineName, slotDate, slotLocation);
                        certificateList.add(certificate);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    public void showDownloadDialog(Certificate certificate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download Certificate");

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Generate a default filename based on the vaccine name and date
        String defaultFilename = certificate.getVaccineName() + "_" + certificate.getSlotDate().replace("/", "-");
        input.setText(defaultFilename);  // Set the default filename

        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String filename = input.getText().toString().trim();
            if (!filename.isEmpty()) {
                downloadCertificate(certificate, filename);
            } else {
                Toast.makeText(this, "Filename cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private void downloadCertificate(Certificate certificate, String filename) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            String userName = documentSnapshot.getString("name");

            // Load the template image and prepare it as a bitmap
            Bitmap templateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.certificate);
            Bitmap mutableBitmap = templateBitmap.copy(Bitmap.Config.ARGB_8888, true);

            // Prepare the Canvas and Paint
            Canvas canvas = new Canvas(mutableBitmap);
            Paint paint = new Paint();
            paint.setTextSize(110);
            paint.setAntiAlias(true);

            int xName = 790, yName = 835;
            int xVaccine = 1250, yVaccine = 990;
            int xDate = 1600, yDate = 1160;
            int xLocation = 1000, yLocation = 1330;

            // Draw text on the template
            canvas.drawText(userName, xName, yName, paint);
            canvas.drawText(certificate.getVaccineName(), xVaccine, yVaccine, paint);
            canvas.drawText(certificate.getSlotDate(), xDate, yDate, paint);
            canvas.drawText(certificate.getSlotLocation(), xLocation, yLocation, paint);

            // Convert the modified bitmap to PDF
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(mutableBitmap.getWidth(), mutableBitmap.getHeight(), 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas pdfCanvas = page.getCanvas();
            pdfCanvas.drawBitmap(mutableBitmap, 0, 0, null);
            pdfDocument.finishPage(page);

            // Use MediaStore to save the PDF to the public Downloads directory
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename + ".pdf");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            ContentResolver resolver = getContentResolver();
            OutputStream fos = null;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    if (uri != null) {
                        fos = resolver.openOutputStream(uri);
                        pdfDocument.writeTo(fos);
                        Toast.makeText(this, "Certificate saved as " + filename + ".pdf", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Fallback for devices below Android 10
                    File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(downloadFolder, filename + ".pdf");
                    fos = new FileOutputStream(file);
                    pdfDocument.writeTo(fos);
                    Toast.makeText(this, "Certificate saved as " + filename + ".pdf", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                Toast.makeText(this, "Error saving certificate", Toast.LENGTH_SHORT).show();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                pdfDocument.close();
            }
        });
    }
}
