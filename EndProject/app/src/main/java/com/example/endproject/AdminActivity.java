package com.example.endproject;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {
        //Admin activity class
    private Spinner adminSpinner;
    private ArrayList<String> trailsList;
    private ApiService apiService;
    private Button bugsBtn, reportBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize views

        adminSpinner = findViewById(R.id.adminSpinner);
        bugsBtn = findViewById(R.id.bugsBtn);
        reportBtn = findViewById(R.id.reportBtn);

        setupImageButtons();
        trailsList = new ArrayList<>();

        // Bugs button click listener
        bugsBtn.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, BugsAdminActivity.class)));

        // Fetch trail names for the spinner
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        fetchTrails();

        // Report button click listener
        reportBtn.setOnClickListener(v -> fetchMostFavoritedTrailsAndCreatePdf());

        // Spinner selection listener
        adminSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTrailName = trailsList.get(position);

                if (!selectedTrailName.isEmpty()) {
                    fetchTrailByName(selectedTrailName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }
//populate the spinner with trails
    private void fetchTrails() {
        trailsList.clear();
        trailsList.add("");

        Call<List<String>> call = apiService.getAllTrails();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    trailsList.addAll(response.body());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AdminActivity.this,
                            android.R.layout.simple_spinner_item,
                            trailsList
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    adminSpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to get trails: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
// Nav bar
    private void setupImageButtons() {
        ImageButton home = findViewById(R.id.homeButton);
        home.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, HomeActivity.class)));

        ImageButton search = findViewById(R.id.searchButton);
        search.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, SearchActivity.class)));

        ImageButton favorite = findViewById(R.id.favoriteButton);
        favorite.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, FavoriteActivity.class)));

        ImageButton profile = findViewById(R.id.profileButton);
        profile.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, ProfileActivity.class)));

        ImageButton admin = findViewById(R.id.adminButton);
        String userName = User.getInstance().getUsername();
        admin.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, AdminActivity.class)));
        if (userName.equals("admin")) {
            admin.setVisibility(View.VISIBLE);
        }
    }
//fetching trails by name for update option
    private void fetchTrailByName(String trailName) {
        Call<Trail> call = apiService.getTrailByName(trailName);
        call.enqueue(new Callback<Trail>() {
            @Override
            public void onResponse(Call<Trail> call, Response<Trail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Trail trail = response.body();
                    Toast.makeText(AdminActivity.this, "Trail: " + trail.getTrailName(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AdminActivity.this, AdminUpdateActivity.class);
                    intent.putExtra("trail", trail);
                    startActivity(intent);
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to fetch trail details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Trail> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
//Creating report of the favorite trails into PDF
    private void fetchMostFavoritedTrailsAndCreatePdf() {
        Call<List<TrailFavorite>> call = apiService.getMostFavoritedTrails();

        call.enqueue(new Callback<List<TrailFavorite>>() {
            @Override
            public void onResponse(Call<List<TrailFavorite>> call, Response<List<TrailFavorite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TrailFavorite> trails = response.body();
                    File pdfFile = createPdfWithTrails(trails);

                    if (pdfFile != null) {
                        showPdf(pdfFile);
                    } else {
                        Toast.makeText(AdminActivity.this, "Failed to create PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to fetch trails: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TrailFavorite>> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
//Settings for PDF file
    private File createPdfWithTrails(List<TrailFavorite> trails) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        int yPosition = 50; // Start position for text

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        // Paint for titles
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(18);
        titlePaint.setFakeBoldText(true); // Bold text
        titlePaint.setUnderlineText(true); // Underlined text

        // Paint for regular text
        paint.setTextSize(16);

        // Add title
        canvas.drawText("רשימת מסלולים מועדפים", 50, yPosition, titlePaint);
        yPosition += 40;

        // Add table headers with bold and underlined text
        canvas.drawText("שם מסלול:", 50, yPosition, titlePaint);
        canvas.drawText("מספר המועדפים:", 300, yPosition, titlePaint);
        yPosition += 30;

        // Add data rows
        for (TrailFavorite trail : trails) {
            String trailName = (trail.getTrailName() != null) ? trail.getTrailName() : "Unknown";
            String count = String.valueOf(trail.getCount());

            canvas.drawText(trailName, 50, yPosition, paint);
            canvas.drawText(count, 300, yPosition, paint);
            yPosition += 20;

            // Start a new page if content exceeds the page height
            if (yPosition > 800) {
                pdfDocument.finishPage(page);
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                yPosition = 50;

                // Redraw the headers on the new page
                canvas.drawText("שם מסלול:", 50, yPosition, titlePaint);
                canvas.drawText("מספר המועדפים:", 300, yPosition, titlePaint);
                yPosition += 30;
            }
        }

        pdfDocument.finishPage(page);

        // Save the PDF to cache
        File pdfFile = new File(getCacheDir(), "most_favorited_trails.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            pdfDocument.close();
            return pdfFile;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
//option to view PDF file
    private void showPdf(File pdfFile) {
        Uri pdfUri = FileProvider.getUriForFile(
                AdminActivity.this,
                getPackageName() + ".provider",
                pdfFile
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No application available to view PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}