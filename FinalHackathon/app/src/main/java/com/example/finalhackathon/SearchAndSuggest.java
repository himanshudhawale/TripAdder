package com.example.finalhackathon;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class SearchAndSuggest extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportPlaceAutocompleteFragment placeAutoComplete;
    Button findRestaurent;
    ArrayList<LatLng> suggestionList;
    HashMap<LatLng,String> mapOfData=new HashMap<>();
    HashMap<LatLng,String> selectedData=new HashMap<>();
    Marker myMarker;
    DatabaseReference myRef;
    String myUser;
    String userID;
    DatabaseReference ref;
    ArrayList<Trip> tripList=new ArrayList<>();
    ArrayList<String> placeList=new ArrayList<>();
    Place myPlace;
Button btnDate, buttonToNext;
static int year, month, day;

int flag=0;
String tripName="abc";
ArrayList<Trip> tripListOfTrips=new ArrayList<>();



@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_and_suggest);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findRestaurent=findViewById(R.id.btnRestaurant);
        findRestaurent.setVisibility(View.INVISIBLE);
        btnDate=findViewById(R.id.button3);
        buttonToNext=findViewById(R.id.button4);
        buttonToNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchAndSuggest.this, ShowTrip.class);
                startActivity(intent);
            }
        });


    btnDate.setVisibility(View.INVISIBLE);

    FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
    myUser=currentUser.getDisplayName();
        userID=currentUser.getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("trips");





        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        myRef.child(tripName).addValueEventListener(new ValueEventListener() {
            ArrayList<Trip> x=new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot x1: dataSnapshot.getChildren())
                {
                    Trip trip1= x1.getValue(Trip.class);

                    x.add(trip1);
                }
                tripList=x;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("USA")
                .build();


        placeAutoComplete = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setFilter(typeFilter);//setting this to find place only in the USA
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                // TODO: Get info about the selected place.
                Log.i("demo", "Place: " + place.getName());

                tripListOfTrips.clear();
                mMap.clear();
                flag=0;

                if(tripList.size()==0)
                {
                    myPlace=place;
                }

                for(int i=0; i<tripList.size(); i++)
                {
                    if(tripList.get(i).place.equals(place.getName()))
                    {
                       //Do nothing
                    }
                    else
                    {
                        myPlace=place;
                    }
                }



               addMarker(place);
               findRestaurent.setVisibility(View.VISIBLE);
               btnDate.setVisibility(View.VISIBLE);
               findRestaurent.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       flag=1;
                       findRest(place.getLatLng().latitude,place.getLatLng().longitude);

                       
                       
                   }
               });

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("demo", "An error occurred: " + status);
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

      //  int padd= (int) ((getResources().getDisplayMetrics().widthPixels)*0.01);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.27);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);


      //  CameraUpdate cu=CameraUpdateFactory.newLatLngBounds(bounds, getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels,padd);

        mMap.animateCamera(cu);

        showAlert();


    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchAndSuggest.this);
        final EditText editText=new EditText(this);

        builder.setMessage("Enter the trip name")
                .setTitle("Hello " + myUser + ",")
                .setView(editText)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        tripName=editText.getText().toString();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tripName=null;
                        showAlert();

                    }
                });
        final AlertDialog alert = builder.create();

        // Create the AlertDialog object and return it
alert.show();
    }

    private void findRest(double latitude, double longitude) {

        mMap.clear();
     //   Intent i = getIntent();
        String type = "Restaurant";

        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append("1609.34");
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyAvX6Y45JwSwa1LK9jbolqWqJ8rJpwCx3A");

//JsonObjectRequest request= new JsonObjectRequest()



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, googlePlacesUrl.toString(), null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
                    parseRestData(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        SingletonRequestQueue.getInstance(this).addToRequestQueue(request);


    }

    private void parseRestData(JSONObject response) throws JSONException {
        Log.d("demo",response.toString());
        mapOfData.clear();
        JSONArray resultsArray=response.getJSONArray("results");

        for(int i=0;i<resultsArray.length();i++)
        {
            JSONObject locationObject=resultsArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
            LatLng latLng=new LatLng(locationObject.getDouble("lat"),locationObject.getDouble("lng"));
            String nameOfPlace=resultsArray.getJSONObject(i).getString("name");

           // suggestionList.add(latLng);
            mapOfData.put(latLng,nameOfPlace);
        }
        Log.d("demoo",mapOfData.toString());
        addToMap(mapOfData);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
  }

    public void addMarker(Place p){

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(p.getLatLng());
        markerOptions.title(p.getName()+"");
        //   markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));


    /*    Circle circle = mMap.addCircle(new CircleOptions()
                .center(p.getLatLng())
                .radius(1609.34)
                .strokeColor(Color.RED));
               // .fillColor(Color.BLUE));
*/

        int padd= (int) ((getResources().getDisplayMetrics().widthPixels)*0.01);

        CameraUpdate cu=CameraUpdateFactory.newLatLngBounds(p.getViewport(), getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels,padd);


        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(p.getLatLng()));
        mMap.animateCamera(cu);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if(flag==1) {
                        if (day == 0) {
                            final Calendar c = Calendar.getInstance();
                            year = c.get(Calendar.YEAR);
                            month = c.get(Calendar.MONTH);
                            day = c.get(Calendar.DAY_OF_MONTH);
                        }


                            final String placeName = marker.getTitle();
                            final LatLng position = marker.getPosition();
                            AlertDialog.Builder builder = new AlertDialog.Builder(SearchAndSuggest.this);
                            builder.setMessage("Do you want to add " + placeName + " to the trip?")
                                    .setTitle("Create trip")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            selectedData.put(position, placeName);

                                            Trip trip = new Trip();
                                            trip.place = placeName;
                                            trip.latitude = position.latitude;
                                            trip.longitude = position.longitude;
                                            trip.year = year;
                                            trip.month = month;
                                            trip.day = day;
                                            trip.tripName = tripName;
                                            trip.city = myPlace.getName().toString();

                                            tripListOfTrips.add(trip);


                                            myRef.child(tripName).setValue(tripListOfTrips);

                                        }
                                    })

                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(SearchAndSuggest.this, "Trip not created", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                            AlertDialog alert = builder.create();
                            alert.show();

                        }


                    return true;
                }
            });




    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker();
            return  dialog;
        }

        public void onDateSet(DatePicker view, int year1, int month1, int day1) {
            //btnDate.setText(ConverterDate.ConvertDate(year, month + 1, day));

            year=year1;
            month=month1+1;
            day=day1;


        }




    }


}
