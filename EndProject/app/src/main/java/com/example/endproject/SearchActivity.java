package com.example.endproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.appcompat.app.AppCompatActivity;
//Initialize parameters

public class SearchActivity extends AppCompatActivity {
    private ApiService apiService;
    private Trail nullTrail;
    private AutoCompleteTextView trailAutoCompleteTextView;
    private Button buttonNorth, buttonCenter, buttonSouth;
    private Spinner northSpinner, centerSpinner, southSpinner;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize AutoCompleteTextView and other UI components
        trailAutoCompleteTextView = findViewById(R.id.trailAutoCompleteTextView);
        buttonNorth = findViewById(R.id.buttonNorth);
        buttonCenter = findViewById(R.id.buttonCenter);
        buttonSouth = findViewById(R.id.buttonSouth);
        northSpinner = findViewById(R.id.NorthSpinner);
        centerSpinner = findViewById(R.id.CenterSpinner);
        southSpinner = findViewById(R.id.SouthSpinner);


        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Initialize nullTrail
        nullTrail = new Trail("", "", "", 0.0, " ", " ", " ", " ", "", 0, "");

        // Call the method to get all trail names for autocomplete suggestions
        getAllTrailsForAutoComplete();

        // Set up button click listeners for spinners
        setupSpinnerButtons();

        // Image button setup
        setupImageButtons();

        // Load trails into spinners
        getNorthTrails();
        getCenterTrails();
        getSouthTrails();
    }
//Handling the spinners options
    private void setupSpinnerButtons() {
        buttonNorth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                northSpinner.setVisibility(View.VISIBLE);
                centerSpinner.setVisibility(View.INVISIBLE);
                southSpinner.setVisibility(View.INVISIBLE);
            }
        });

        buttonCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                northSpinner.setVisibility(View.INVISIBLE);
                centerSpinner.setVisibility(View.VISIBLE);
                southSpinner.setVisibility(View.INVISIBLE);
            }
        });

        buttonSouth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                northSpinner.setVisibility(View.INVISIBLE);
                centerSpinner.setVisibility(View.INVISIBLE);
                southSpinner.setVisibility(View.VISIBLE);
            }
        });
    }
//Nav bar
    private void setupImageButtons() {
        ImageButton home = findViewById(R.id.homeButton);
        home.setOnClickListener(v -> startActivity(new Intent(SearchActivity.this, HomeActivity.class)));

        ImageButton search = findViewById(R.id.searchButton);
        search.setOnClickListener(v -> startActivity(new Intent(SearchActivity.this, SearchActivity.class)));

        ImageButton favorite = findViewById(R.id.favoriteButton);
        favorite.setOnClickListener(v -> startActivity(new Intent(SearchActivity.this, FavoriteActivity.class)));

        ImageButton profile = findViewById(R.id.profileButton);
        profile.setOnClickListener(v -> startActivity(new Intent(SearchActivity.this, ProfileActivity.class)));

        ImageButton admin = findViewById(R.id.adminButton);

        String userName = User.getInstance().getUsername();

        admin.setOnClickListener(v -> startActivity(new Intent(SearchActivity.this, AdminActivity.class)));
        if (userName.equals("admin")) {
            admin.setVisibility(View.VISIBLE);
        }
    }
//To avoid force query
    public void replaceLastWithFirst(List<Trail> trails) {
        if (trails != null && trails.size() > 1) {
            Trail first = trails.get(0);
            Trail last = trails.get(trails.size() - 1);
            trails.set(0, last);
            trails.set(trails.size() - 1, first);
        }
    }
//Redirect to the chosen trail view
    private void getTrailByName(String trailName) {
        Call<Trail> call = apiService.getTrailByName(trailName);

        call.enqueue(new Callback<Trail>() {
            @Override
            public void onResponse(Call<Trail> call, Response<Trail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Trail trail = response.body();

                    // Create an intent to redirect to trailViewActivity
                    Intent intent = new Intent(SearchActivity.this, trailViewActivity.class);
                    intent.putExtra("trail", trail); // Pass the Trail object to the new activity
                    startActivity(intent);
                } else {
                    Log.e("SearchActivity", "Failed to get trail: " + response.message());
                    Toast.makeText(SearchActivity.this, "Trail not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Trail> call, Throwable t) {
                Log.e("SearchActivity", "API call failed: " + t.getMessage());
                Toast.makeText(SearchActivity.this, "Error fetching trail: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
//Populate the spinner
    private void getNorthTrails() {
        Call<List<Trail>> call = apiService.getNorthTrails();

        call.enqueue(new Callback<List<Trail>>() {
            @Override
            public void onResponse(Call<List<Trail>> call, Response<List<Trail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Trail> trails = response.body();
                    trails.add(nullTrail);
                    replaceLastWithFirst(trails);
                    populateSpinnerWithTrails(R.id.NorthSpinner, trails);
                } else {
                    Log.e("MainActivity", "Failed to get trails: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Trail>> call, Throwable t) {
                Log.e("MainActivity", "API call failed: " + t.getMessage());
            }
        });
    }
    //Populate the spinner
    private void getCenterTrails() {
        Call<List<Trail>> call = apiService.getCenterTrails();

        call.enqueue(new Callback<List<Trail>>() {
            @Override
            public void onResponse(Call<List<Trail>> call, Response<List<Trail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Trail> trails = response.body();
                    trails.add(nullTrail);
                    replaceLastWithFirst(trails);
                    populateSpinnerWithTrails(R.id.CenterSpinner, trails);
                } else {
                    Log.e("MainActivity", "Failed to get trails: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Trail>> call, Throwable t) {
                Log.e("MainActivity", "API call failed: " + t.getMessage());
            }
        });
    }
    //Populate the spinner
    private void getSouthTrails() {
        Call<List<Trail>> call = apiService.getSouthTrails();

        call.enqueue(new Callback<List<Trail>>() {
            @Override
            public void onResponse(Call<List<Trail>> call, Response<List<Trail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Trail> trails = response.body();
                    trails.add(nullTrail);
                    replaceLastWithFirst(trails);
                    populateSpinnerWithTrails(R.id.SouthSpinner, trails);
                } else {
                    Log.e("MainActivity", "Failed to get trails: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Trail>> call, Throwable t) {
                Log.e("MainActivity", "API call failed: " + t.getMessage());
            }
        });
    }

    private void populateSpinnerWithTrails(int spinnerId, List<Trail> trails) {
        Spinner spinner = findViewById(spinnerId);

        List<String> trailNames = new ArrayList<>();
        for (Trail trail : trails) {
            trailNames.add(trail.getTrailName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trailNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTrailName = trailNames.get(position);

                // Make the API call
                Call<Trail> call = apiService.getTrailByName(selectedTrailName);

                call.enqueue(new Callback<Trail>() {
                    @Override
                    public void onResponse(Call<Trail> call, Response<Trail> response) {
                        if (response.isSuccessful()) {
                            Trail trail = response.body();
                            Toast.makeText(SearchActivity.this, "" + response.isSuccessful(), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SearchActivity.this, trailViewActivity.class);
                            intent.putExtra("trail", trail);
                            startActivity(intent);
                        } else {
                            // Handle the case where the trail is not found
                        }
                    }

                    @Override
                    public void onFailure(Call<Trail> call, Throwable t) {
                        // Handle network failure or other errors
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }
//AutoComplete for the search bar
    private void getAllTrailsForAutoComplete() {
        Call<List<String>> call = apiService.getAllTrails();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    Log.d("SearchActivity", "Raw Response: " + new Gson().toJson(response.body()));

                    if (response.body() != null) {
                        List<String> trailResponses = response.body();
                        List<String> trailNames = new ArrayList<>(trailResponses);

                        setupAutoCompleteTextView(trailNames);
                    } else {
                        Log.e("SearchActivity", "Response body is null");
                    }
                } else {
                    try {
                        Log.e("SearchActivity", "Error Response: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("SearchActivity", "Failed to read error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("SearchActivity", "API call failed: " + t.getMessage());
            }
        });
    }

    private void setupAutoCompleteTextView(List<String> trailNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                SearchActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                trailNames
        );
        trailAutoCompleteTextView.setAdapter(adapter);
        trailAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTrailName = (String) parent.getItemAtPosition(position);
            getTrailByName(selectedTrailName);
        });
    }



}
