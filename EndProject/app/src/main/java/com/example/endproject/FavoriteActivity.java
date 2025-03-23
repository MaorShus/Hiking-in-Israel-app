package com.example.endproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FavoriteActivity extends AppCompatActivity {
//Initialize parameters
    ListView favList;
    FavoriteTrailAdapter adapter;
    ApiService apiInterface; // Retrofit API interface

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        favList = findViewById(R.id.favList);

//Server handler
        apiInterface = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        String user = User.getInstance().getUsername();

        fetchFavoriteTrails(user); // Fetch and display favorite trails

        // Set up navigation buttons (home, search, profile, etc.)
        setupImageButtons();

        favList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTrailName = adapter.getItem(position);
            fetchTrailDetails(selectedTrailName);
        });

    }
//View of the chosen trail
    private void fetchTrailDetails(String trailName) {
        Call<Trail> call = apiInterface.getTrailByName(trailName);
        call.enqueue(new Callback<Trail>() {
            @Override
            public void onResponse(Call<Trail> call, Response<Trail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Trail trail = response.body();
                    Intent intent = new Intent(FavoriteActivity.this, trailViewActivity.class);
                    intent.putExtra("trail", trail);  // Pass Trail object
                    startActivity(intent);
                } else {
                    Toast.makeText(FavoriteActivity.this, "Trail not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Trail> call, Throwable t) {
                Toast.makeText(FavoriteActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
//Creating the favorite list
    private void fetchFavoriteTrails(String userName) {
        Call<List<String>> call = apiInterface.getFavoriteTrails(userName);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> favoriteTrails = response.body();
                    adapter = new FavoriteTrailAdapter(FavoriteActivity.this, favoriteTrails);
                    favList.setAdapter(adapter); // Set adapter to ListView
                } else {
                    Toast.makeText(FavoriteActivity.this, "No favorites found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(FavoriteActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


//Nav bar
    private void setupImageButtons() {
        ImageButton home = findViewById(R.id.homeButton);
        home.setOnClickListener(v -> startActivity(new Intent(FavoriteActivity.this, HomeActivity.class)));

        ImageButton search = findViewById(R.id.searchButton);
        search.setOnClickListener(v -> startActivity(new Intent(FavoriteActivity.this, SearchActivity.class)));

        ImageButton favorite = findViewById(R.id.favoriteButton);
        favorite.setOnClickListener(v -> startActivity(new Intent(FavoriteActivity.this, FavoriteActivity.class)));

        ImageButton profile = findViewById(R.id.profileButton);
        profile.setOnClickListener(v -> startActivity(new Intent(FavoriteActivity.this, ProfileActivity.class)));

        ImageButton admin = findViewById(R.id.adminButton);

        String userName = User.getInstance().getUsername();
        admin.setOnClickListener(v -> startActivity(new Intent(FavoriteActivity.this, AdminActivity.class)));
        if (userName.equals("admin")) {
            admin.setVisibility(View.VISIBLE);
        }


    }
}
