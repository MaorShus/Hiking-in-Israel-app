package com.example.endproject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



//Initialize parameters

public class trailViewActivity extends AppCompatActivity {
    TextView textViewTrailView;
    private MapView mapView;
    Button openGpsBtn;
    Switch mySwitch;
    ApiService apiInterface; // Retrofit API interface
    private static final String PREFS_NAME = "TrailPrefs";
    private static final String SWITCH_STATE_KEY = "SwitchState_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail_view);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);



        apiInterface = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        setupImageButtons();

        // Get the intent and retrieve the Trail object
        Intent intent = getIntent();
        Trail trail = intent.getParcelableExtra("trail");
        String trail_num_str = trail.getTrailNum() + "";
        checkIfFavorite(trail_num_str, User.getInstance().getUsername());
        String trailName = trail.getTrailName();  // Get the trail name
        String username = User.getInstance().getUsername();
        String trainNum = String.valueOf(trail.getTrailNum());
        mySwitch = findViewById(R.id.mySwitch);

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load the saved state of the Switch for this specific trail
        String switchKey = SWITCH_STATE_KEY + trail.getTrailNum();
        boolean isSwitchChecked = sharedPreferences.getBoolean(switchKey, false);
        mySwitch.setChecked(isSwitchChecked);
        //Map view parameters
        mapView = findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(new GeoPoint(0.0, 0.0));
        //Trails filter images
        ImageView imgBike = findViewById(R.id.imgBike);
        ImageView imgPet = findViewById(R.id.imgPet);
        ImageView imgJeep = findViewById(R.id.imgJeep);
        ImageView imgWater = findViewById(R.id.imgWater);
        ImageView imgCamping = findViewById(R.id.imgCamping);

        // Set visibility of icons based on trail attributes
        if (trail.getPet().equals("1"))
            imgPet.setVisibility(View.VISIBLE);
        if (trail.getBike().equals("1"))
            imgBike.setVisibility(View.VISIBLE);
        if (trail.getJeep().equals("1"))
            imgJeep.setVisibility(View.VISIBLE);
        if (trail.getCamping().equals("1"))
            imgCamping.setVisibility(View.VISIBLE);
        if (trail.getWater().equals("1"))
            imgWater.setVisibility(View.VISIBLE);

        // Load GPX file and add polyline to map
        List<GeoPoint> geoPoints = loadGpxFile(trail);
        if (geoPoints != null && !geoPoints.isEmpty()) {
            mapView.getController().setCenter(geoPoints.get(0));
            Polyline polyline = new Polyline();
            polyline.setPoints(geoPoints);
            mapView.getOverlayManager().add(polyline);
        }

        textViewTrailView = findViewById(R.id.textViewTrailView);
        String about = trail.getAbout();
        textViewTrailView.setText(about);

        // Switch handling with API call and saving state to SharedPreferences
        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the state to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(switchKey, isChecked); // Save the state with a unique key per trail
            editor.apply(); // Save changes asynchronously

            if (isChecked) {
                mySwitch.setThumbTintList(ColorStateList.valueOf(Color.RED));
                addToFavorites(trainNum, trailName, username);  // Add trail to favorites
                Toast.makeText(getApplicationContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
            } else {
                mySwitch.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
                removeFromFavorites(trainNum, username);  // Remove trail from favorites
                Toast.makeText(getApplicationContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the button to open GPS apps
        openGpsBtn = findViewById(R.id.openGpsBtn);
        openGpsBtn.setOnClickListener(v -> openGpsNavigation(trail.getTrailName()));
    }
        //Nav bar
    private void setupImageButtons() {
        ImageButton home = findViewById(R.id.homeButton);
        home.setOnClickListener(v -> startActivity(new Intent(trailViewActivity.this, HomeActivity.class)));

        ImageButton search = findViewById(R.id.searchButton);
        search.setOnClickListener(v -> startActivity(new Intent(trailViewActivity.this, SearchActivity.class)));

        ImageButton favorite = findViewById(R.id.favoriteButton);
        favorite.setOnClickListener(v -> startActivity(new Intent(trailViewActivity.this, FavoriteActivity.class)));

        ImageButton profile = findViewById(R.id.profileButton);
        profile.setOnClickListener(v -> startActivity(new Intent(trailViewActivity.this, ProfileActivity.class)));

        ImageButton admin = findViewById(R.id.adminButton);

        String userName = User.getInstance().getUsername();
        admin.setOnClickListener(v -> startActivity(new Intent(trailViewActivity.this, AdminActivity.class)));
        if (userName.equals("admin")) {
            admin.setVisibility(View.VISIBLE);
        }


    }
    // Method to open GPS apps like Waze or Google Maps
    private void openGpsNavigation(String location) {
        try {
            // Try to open Waze if installed
            String wazeUri = "https://waze.com/ul?q=" + Uri.encode(location);
            Intent wazeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wazeUri));
            wazeIntent.setPackage("com.waze");
            startActivity(wazeIntent);
        } catch (ActivityNotFoundException e) {
            try {
                // Fall back to Google Maps if Waze is not installed
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } catch (ActivityNotFoundException ex) {
                // If neither Waze nor Google Maps are installed, show a message
                Toast.makeText(this, "Please install Waze or Google Maps to navigate", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Method to load GPX file from assets
    private List<GeoPoint> loadGpxFile(Trail trail) {
        List<GeoPoint> geoPoints = new ArrayList<>();
        try {
            InputStream inputStream = getAssets().open(trail.getTrailFileName());
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("trkpt")) {
                    String lat = parser.getAttributeValue(null, "lat");
                    String lon = parser.getAttributeValue(null, "lon");
                    GeoPoint geoPoint = new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lon));
                    geoPoints.add(geoPoint);
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return geoPoints;
    }

    // Method to add trail to favorites using Retrofit
    private void addToFavorites(String trailNum, String trailName, String username) {
        FavoriteTrail f1 = new FavoriteTrail(trailNum, trailName, username);
        Call<Void> call = apiInterface.addFavoriteTrail(f1);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Added to favorites successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method to remove trail from favorites using Retrofit
    private void removeFromFavorites(String trailNum, String username) {
        Call<Void> call = apiInterface.deleteFavoriteTrail(trailNum, username);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Removed from favorites successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
        //Switch sign - checking
    public void checkIfFavorite(String trailNum, String username) {
        Call<CountResponse> call = apiInterface.countFavorites(trailNum, username);
        call.enqueue(new Callback<CountResponse>() {
            @Override
            public void onResponse(Call<CountResponse> call, Response<CountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().getNumOfRows();
                    // Set switch based on count
                    mySwitch.setChecked(count > 0); // Checked if count is greater than zero
                } else {
                    mySwitch.setChecked(false); // Default to unchecked
                }
            }

            @Override
            public void onFailure(Call<CountResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
                mySwitch.setChecked(false); // Default to unchecked on failure
            }
        });
    }


}