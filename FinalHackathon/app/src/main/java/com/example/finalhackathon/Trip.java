package com.example.finalhackathon;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.HashMap;

public class Trip  {
    public Double latitude, longitude;
    public String tripName, place, city;
    public int year, month, day;

    @Override
    public String toString() {
        return "Trip{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", tripName='" + tripName + '\'' +
                ", place='" + place + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }
}
