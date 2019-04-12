package com.example.finalhackathon;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowMarkers extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String key_tripName;
    ArrayList<Trip> tripList=new ArrayList<>();
    HashMap<LatLng, String> myHashMap=new HashMap<>();
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_markers);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        key_tripName = getIntent().getExtras().getString(MyAdapter.MY_KEY);

        myRef = FirebaseDatabase.getInstance().getReference("trips").child(key_tripName);
        Log.d("showME", key_tripName);

        fetchList();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void fetchList()
    {
        myRef.addValueEventListener(new ValueEventListener() {
            ArrayList<Trip> tripHain=new ArrayList<>();


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {

                    /*for (DataSnapshot y : x.getChildren()) {
                        Trip trip = y.getValue(Trip.class);
                        tripHain.add(trip);
                    }*/
                    /*tripList = tripHain;

                    for(int i=0; i<tripList.size(); i++)
                    {
                        if(tripList.get(i).tripName.equals(key_tripName)) {
                            Double lat = tripList.get(i).latitude;
                            Double lon = tripList.get(i).longitude;
                            LatLng myLatLng = new LatLng(lat, lon);
                            myHashMap.put(myLatLng, tripList.get(i).place);
                        }
                    }
                    Log.d("showToMe",  myHashMap.toString());
*/
                    Trip trip=x.getValue(Trip.class);
                    LatLng latLng= new LatLng(trip.latitude,trip.longitude);
                    myHashMap.put(latLng,trip.place);

                    mMap.clear();
                    addToMap(myHashMap);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void addToMap(HashMap<LatLng,String> mapOfData) {

        for(LatLng latLng:mapOfData.keySet())
        {
            mMap.addMarker(new MarkerOptions().position(latLng).title(mapOfData.get(latLng)));
        }

        LatLngBounds.Builder builder=new LatLngBounds.Builder();

        for(LatLng latLng:mapOfData.keySet()) {

            builder.include(latLng);
        }
        LatLngBounds bounds=builder.build();

       // int padd= (int) ((getResources().getDisplayMetrics().widthPixels)*0.01);

       // CameraUpdate cu=CameraUpdateFactory.newLatLngBounds(bounds, getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels,padd);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        //int padding = 0; // offset from edges of the map in pixels
      //  CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);


    }



}