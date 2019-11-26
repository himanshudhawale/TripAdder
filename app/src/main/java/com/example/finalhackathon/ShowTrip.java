package com.example.finalhackathon;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ShowTrip extends AppCompatActivity {

    ListView listView;
    MyAdapter myAdapter;
    ArrayList<Trip> tripList=new ArrayList<>();
    DatabaseReference myRef;
    ArrayList<MyObject> myObjectArrayList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_show_trip);

        listView=findViewById(R.id.listViewId);

        this.setTitle("TripAdder");


        myRef=FirebaseDatabase.getInstance().getReference("trips");

        getSomething();





      //  myAdapter.notifyDataSetChanged();


    }


    public void getSomething()
    {
        myRef.addValueEventListener(new ValueEventListener() {
            ArrayList<Trip> tripHain=new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot x: dataSnapshot.getChildren()) {

                    for (DataSnapshot y : x.getChildren()) {
                        Trip trip = y.getValue(Trip.class);
                        tripHain.add(trip);
                    //    Log.d("showME",trip.toString());
                    }

                    tripList = tripHain;



                }
                int myFlag=0;

                for(int i=0; i<tripList.size(); i++)
                {

                    myFlag=0;
                    if(myObjectArrayList.size()==0)
                    {

                        MyObject myObject=new MyObject();
                        myObject.tripName=tripList.get(i).tripName;
                        myObject.cityName=tripList.get(i).city;
                        myObjectArrayList.add(myObject);

                    }
                    else {
                        for (int j = 0; j < myObjectArrayList.size(); j++) {
                            if ((myObjectArrayList.get(j).tripName.equals(tripList.get(i).tripName))) {
                                myFlag=1;
                            }
                        }

                        if(myFlag==0) {
                            MyObject myObject = new MyObject();
                            myObject.tripName = tripList.get(i).tripName;
                            myObject.cityName = tripList.get(i).city;
                            myObjectArrayList.add(myObject);
                        }
                    }
                }

                myAdapter=new MyAdapter(ShowTrip.this, R.layout.my_layout, myObjectArrayList);
                listView.setAdapter(myAdapter);
                Log.d("showME", String.valueOf(tripList.size()));

              /* for(int i=0;i<tripList.size(); i++) {

                    Log.d("show4ME", tripList.get(i).toString());

                }*/




            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
