package com.example.endproject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

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

public class Check extends AppCompatActivity {
    private MapView mapView;

    private ImageButton home,search,favorite,profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        home = findViewById(R.id.homeButton);
        search = findViewById(R.id.searchButton);
        favorite = findViewById(R.id.favoriteButton);
        profile = findViewById(R.id.profileButton);


        mapView = findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(new GeoPoint(0.0, 0.0));

        List<GeoPoint> geoPoints = loadGpxFile();
        if (geoPoints != null && !geoPoints.isEmpty()) {
            mapView.getController().setCenter(geoPoints.get(0));
            Polyline polyline = new Polyline();
            polyline.setPoints(geoPoints);
            mapView.getOverlayManager().add(polyline);
        }


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start SecondActivity
                Intent intent = new Intent(Check.this, Check.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start SecondActivity
                Intent intent = new Intent(Check.this, SearchActivity.class);
                startActivity(intent);
            }


        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start SecondActivity
                Intent intent = new Intent(Check.this, FavoriteActivity.class);
                startActivity(intent);
            }


        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start SecondActivity
                Intent intent = new Intent(Check.this, ProfileActivity.class);
                startActivity(intent);
            }


        });
    }

    private List<GeoPoint> loadGpxFile() {
        List<GeoPoint> geoPoints = new ArrayList<>();
        try {
            InputStream inputStream = getAssets().open("הר מירון.gpx");
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


}