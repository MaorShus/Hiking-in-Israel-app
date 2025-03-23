package com.example.endproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Initialize parameters

public class HomeActivity extends AppCompatActivity {
    private ListView homeList;
    private ApiService apiService; // Retrofit API interface

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeList = findViewById(R.id.homeList);

        // Initialize Retrofit

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Displaying trails ListView in home page
        final List<String> trailNames = new ArrayList<>();
        trailNames.add("הר מירון");
        trailNames.add("יער ירושלים");
        trailNames.add("נחל אוג");
        trailNames.add("יער-אודם");

        final int[] images = {
                R.drawable.merion,
                R.drawable.jerusalemforest,
                R.drawable.ogsmallriver,
                R.drawable.odemforest
        };

        // Set the adapter for the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 0, trailNames) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
                }

                // Get the current trail name and image
                String trailName = getItem(position);
                int imageResource = images[position];

                // Set the TextView
                TextView itemText = convertView.findViewById(R.id.item_text);
                itemText.setText(trailName);

                // Set the ImageView
                ImageView itemImage = convertView.findViewById(R.id.item_image);
                itemImage.setImageResource(imageResource);

                return convertView;
            }
        };

        homeList.setAdapter(adapter);

        // Set an item click listener on the ListView
        homeList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTrailName = trailNames.get(position);
            fetchTrailDetails(selectedTrailName);
        });

        // Setup the image buttons
        setupImageButtons();
    }
//If trail "clicked" intent to the trail view activity
    private void fetchTrailDetails(String trailName) {
        Call<Trail> call = apiService.getTrailByName(trailName);
        call.enqueue(new Callback<Trail>() {
            @Override
            public void onResponse(Call<Trail> call, Response<Trail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Trail trail = response.body();
                    Intent intent = new Intent(HomeActivity.this, trailViewActivity.class);
                    intent.putExtra("trail", trail);  // Pass the Trail object
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Trail not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Trail> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
//Nav bar
    private void setupImageButtons() {
        ImageButton home = findViewById(R.id.homeButton);
        home.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, HomeActivity.class)));

        ImageButton search = findViewById(R.id.searchButton);
        search.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SearchActivity.class)));

        ImageButton favorite = findViewById(R.id.favoriteButton);
        favorite.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FavoriteActivity.class)));

        ImageButton profile = findViewById(R.id.profileButton);
        profile.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        ImageButton admin = findViewById(R.id.adminButton);

        String userName = User.getInstance().getUsername();
        admin.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AdminActivity.class)));
        if (userName.equals("admin")) {
            admin.setVisibility(View.VISIBLE);
        }


    }
}
