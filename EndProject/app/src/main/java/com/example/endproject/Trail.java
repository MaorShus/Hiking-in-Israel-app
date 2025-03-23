package com.example.endproject;

import android.os.Parcel;
import android.os.Parcelable;

public class Trail implements Parcelable {
    private String district;
    private String trail_name;
    private String trail_file_name;
    private double length;
    private String water;
    private String camping;
    private String bike;
    private String jeep;
    private String pet;
    private int trail_num;
    private String about;

    // Constructor
    public Trail(String district, String trail_name, String trail_file_name, double length, String water, String camping,
                 String bike, String jeep, String pet, int trail_num, String about) {
        this.district = district;
        this.trail_name = trail_name;
        this.length = length;
        this.water = water;
        this.camping = camping;
        this.bike = bike;
        this.jeep = jeep;
        this.pet = pet;
        this.trail_num = trail_num;
        this.about = about;
        this.trail_file_name = trail_file_name;
    }

    // Parcelable implementation
    protected Trail(Parcel in) {
        district = in.readString();
        trail_name = in.readString();
        trail_file_name = in.readString();  // Make sure this field is read from the parcel
        length = in.readDouble();
        water = in.readString();
        camping = in.readString();
        bike = in.readString();
        jeep = in.readString();
        pet = in.readString();
        trail_num = in.readInt();
        about = in.readString();  // Ensure about is read properly
    }

    public static final Creator<Trail> CREATOR = new Creator<Trail>() {
        @Override
        public Trail createFromParcel(Parcel in) {
            return new Trail(in);
        }

        @Override
        public Trail[] newArray(int size) {
            return new Trail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(district);
        dest.writeString(trail_name);
        dest.writeString(trail_file_name);  // Ensure this is written to the parcel
        dest.writeDouble(length);
        dest.writeString(water);
        dest.writeString(camping);
        dest.writeString(bike);
        dest.writeString(jeep);
        dest.writeString(pet);
        dest.writeInt(trail_num);
        dest.writeString(about);  // Ensure about is written properly
    }

    // Getters and Setters
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public int getTrailNum() { return trail_num; }

    public String getTrailName() { return trail_name; }
    public void setTrailName(String trail_name) { this.trail_name = trail_name; }

    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }

    public String getWater() { return water; }
    public void setWater(String water) { this.water = water; }

    public String getPet() { return pet; }
    public void setPet(String pet) { this.pet = pet; }

    public String getJeep() { return jeep; }
    public void setJeep(String jeep) { this.jeep = jeep; }

    public String getBike() { return bike; }
    public void setBike(String bike) { this.bike = bike; }

    public String getCamping() { return camping; }
    public void setCamping(String camping) { this.camping = camping; }


    public String getAbout() {
        if (about == null) {
            return "This is Null About";
        } else if (about.length() == 0) {
            return "This is Empty About";
        }
        return about;
    }

    @Override
    public String toString() {
        return trail_name; // or another localized string
    }

    public void setAbout(String about) { this.about = about; }


    public String getTrailFileName() { return trail_file_name; }

    // Add other getters and setters...
}
