package com.example.endproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

///Initialize parameters

public class AdminUpdateActivity extends AppCompatActivity {
    EditText trailNameTextView, aboutTextView;
    CheckBox waterBox, campingBox, petBox, bikeBox, jeepBox;
    Button updateBtn;
    ApiService apiService;
    Trail trail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update);

        Intent intent = getIntent();
        trail = intent.getParcelableExtra("trail");//receiving object from the last page

        trailNameTextView = findViewById(R.id.trailNameTextView);
        aboutTextView = findViewById(R.id.aboutTextView);
        waterBox = findViewById(R.id.waterBox);
        campingBox = findViewById(R.id.campingBox);
        petBox = findViewById(R.id.petBox);
        bikeBox = findViewById(R.id.bikeBox);
        jeepBox = findViewById(R.id.jeepBox);
        updateBtn = findViewById(R.id.updateBtn);

        // Set the initial values of the fields from the Trail object
        if (trail != null) {
            trailNameTextView.setText(trail.getTrailName());
            aboutTextView.setText(trail.getAbout());

            waterBox.setChecked("1".equals(trail.getWater()));
            campingBox.setChecked("1".equals(trail.getCamping()));
            petBox.setChecked("1".equals(trail.getPet()));
            bikeBox.setChecked("1".equals(trail.getBike()));
            jeepBox.setChecked("1".equals(trail.getJeep()));
        }

        // Initialize the Retrofit API service
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Set up the button click listener to update the trail
        updateBtn.setOnClickListener(v -> {
            // Get updated values from the input fields
            String updatedTrailName = trailNameTextView.getText().toString();
            String updatedAbout = aboutTextView.getText().toString();

            String water = waterBox.isChecked() ? "1" : "0";
            String camping = campingBox.isChecked() ? "1" : "0";
            String pet = petBox.isChecked() ? "1" : "0";
            String bike = bikeBox.isChecked() ? "1" : "0";
            String jeep = jeepBox.isChecked() ? "1" : "0";

            // Update the trail object with new values
            trail.setTrailName(updatedTrailName);
            trail.setAbout(updatedAbout);
            trail.setWater(water);
            trail.setCamping(camping);
            trail.setPet(pet);
            trail.setBike(bike);
            trail.setJeep(jeep);

            // Make the PUT request to update the trail on the server
            updateTrail(trail);
        });
    }
       //Function to update the trails values
    private void updateTrail(Trail updatedTrail) {
        Call<Void> call = apiService.updateTrail(updatedTrail.getTrailNum(), updatedTrail);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminUpdateActivity.this, "Trail updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Optionally finish the activity after successful update
                } else {
                    Toast.makeText(AdminUpdateActivity.this, "Failed to update trail. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminUpdateActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
