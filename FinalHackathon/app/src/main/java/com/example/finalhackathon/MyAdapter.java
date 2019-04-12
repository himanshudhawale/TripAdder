package com.example.finalhackathon;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<Trip> {

    ArrayList<MyObject> list;
    int mresource;
    Context mContext;
    public static String MY_KEY="key";

    public MyAdapter(@NonNull  Context context,  int resource, @NonNull ArrayList<MyObject> list) {
        super(context, resource);

        this.list=list;
        this.mContext=context;
        this.mresource=resource;


    }


    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView,@NonNull ViewGroup parent)
    {



        final MyObject trip=list.get(position);

        Log.d("showME", trip.toString());
        if(convertView==null)
        {
            convertView=LayoutInflater.from(getContext()).inflate(R.layout.my_layout, parent, false);
        }

        TextView textView=convertView.findViewById(R.id.tripNameId);
        TextView textView1=convertView.findViewById(R.id.cityNameId);
        Button button=convertView.findViewById(R.id.buttonShowId);


        textView.setText(trip.tripName);
        textView1.setText(trip.cityName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(mContext, ShowMarkers.class);
                intent.putExtra(MY_KEY,trip.tripName);
                mContext.startActivity(intent);

            }
        });



        for(int i=0;i<list.size(); i++) {

            Log.d("showME", list.get(i).toString());

        }


        return convertView;

    }
}
