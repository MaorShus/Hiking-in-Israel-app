package com.example.endproject;

import com.google.gson.annotations.SerializedName;

public class TrailFavorite {

    @SerializedName("trail_name") // Maps JSON "trail_name" to Java "trailName"
    private String trailName;

    @SerializedName("count") // Maps JSON "count" to Java "count"
    private int count;

    public String getTrailName() {
        return trailName;
    }

    public void setTrailName(String trailName) {
        this.trailName = trailName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}