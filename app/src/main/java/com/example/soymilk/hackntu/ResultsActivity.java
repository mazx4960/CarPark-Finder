package com.example.soymilk.hackntu;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    double[] destCoords;
    CarparkDatabase db;
    Spinner dropDownBox;
    ListView listView;
    TextView destView;
    String destName;

    List<Carpark> listOfNearbyCarparks;

    // vars for calculating boundary
    double maxlat;
    double minlat;
    double maxlng;
    double minlng;

    double radius;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        /***** getIntent() ****/
        destCoords = getIntent().getDoubleArrayExtra("destCoords");
        destName = getIntent().getStringExtra("destName");

        /***** getDatabase ****/
        db = Room.databaseBuilder(getApplicationContext(), CarparkDatabase.class, "CarparksDB").allowMainThreadQueries().build();

        /**** UI ***/
        dropDownBox = (Spinner) findViewById(R.id.spinner);
        listView = (ListView) findViewById(R.id.resultsList);
        destView = (TextView) findViewById(R.id.destination);
        destView.setText(destName);

        /**** MATH *****/
        radius = 1.0; // TODO: Expose to UI for user input
        calcBoundaries(destCoords[0], destCoords[1], radius);

        /*** getNearbyCarparks ****/
        getNearybyCarparks();

        /**** SPINNER *****/
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sorts_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        dropDownBox.setAdapter(adapter);
        dropDownBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                updateUI(position);
            }
        });
    }

    private void getNearybyCarparks(){
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                listOfNearbyCarparks = db.carpackDAO().getNearbyCarparks(minlat, maxlat, minlng, minlng);
                return null;
            }
        };
        task.execute();
    }

    private void calcBoundaries(double lat, double lng, double radius){


        // TODO: Add formula (Done but not tested)

        // earth's radius in km = ~6371
        double earthRadius = 6371;
        double angrad = radius / earthRadius;

        // latitude boundaries
        maxlat = lat + Math.toDegrees(angrad);
        minlat = lat - Math.toDegrees(angrad);

        // longitude boundaries (longitude gets smaller when latitude increases)
        maxlng = lng + Math.toDegrees(angrad / Math.cos(Math.toRadians(lat)));
        minlng = lng - Math.toDegrees(angrad / Math.cos(Math.toRadians(lat)));


    }

    private void updateUI(int position) {

        //listOfNearbyCarparks.get(position);


    }


}
