package com.example.soymilk.hackntu;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
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
    SeekBar radiusBar;

    List<Carpark> listOfNearbyCarparks;
    ArrayAdapter<Carpark> carparkArrayAdapter;

    // vars for calculating boundary
    double maxlat;
    double minlat;
    double maxlng;
    double minlng;

    double radius;

    /*** TODO:
     * BUG: Multiple carparks with same Development name
     *
     */


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
        radiusBar = (SeekBar) findViewById(R.id.seekBar);
        destView.setText(destName);

        /**** MATH *****/
        radius = 0.3;
        calcBoundaries(destCoords[0], destCoords[1], radius);

        /*** getNearbyCarparks ****/
        getNearybyCarparks();

        /*** User input for radius ****/
        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = progress*0.3;
                calcBoundaries(destCoords[0], destCoords[1], radius);
                getNearybyCarparks();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        /**** SPINNER *****/
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sorts_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        dropDownBox.setAdapter(adapter);


    }

    private void getNearybyCarparks(){
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                listOfNearbyCarparks = db.carpackDAO().getNearbyCarparks(minlat, maxlat, minlng, maxlng);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                /***** ListView ****/
                CarparkAdapter carparkAdapter = new CarparkAdapter(getApplicationContext(), R.layout.list_item, listOfNearbyCarparks);
                listView.setAdapter(carparkAdapter);
            }
        };
        task.execute();
    }

    private void calcBoundaries(double lat, double lng, double radius){


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

        listOfNearbyCarparks.get(position);

    }

    class CarparkAdapter extends ArrayAdapter<Carpark>{
        public CarparkAdapter(Context context, int resource, List<Carpark> carparkList) {
            super(context, 0, carparkList);
        }


        @Override
        public View getView(int position, View convertView,  ViewGroup parent) {
            View listItemView = convertView;
            if(convertView == null){
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            Carpark carpark = getItem(position);

            /**** findViewById ****/
            TextView locName = (TextView) listItemView.findViewById(R.id.locName);
            TextView price = (TextView) listItemView.findViewById(R.id.price);
            TextView availLots = (TextView) listItemView.findViewById(R.id.availLots);
            TextView distance = (TextView) listItemView.findViewById(R.id.distance);

            locName.setText(carpark.development);
            price.setText(carpark.lotType); // TODO: set number of dollar signs by lotType
            availLots.setText(""+carpark.availableLots);
            distance.setText(getCarparkDistance(carpark.latitude, carpark.longitude));

            return listItemView;

        }
    }

    private String getCarparkDistance(double latitude, double longitude) {

        double destLat = destCoords[0];
        double destLng = destCoords[1];

        // convert latitude/longitude degrees for both coordinates
        // to radians: radian = degree * π / 180
        destLat = Math.toRadians(destLat);
        destLng = Math.toRadians(destLng);
        latitude = Math.toRadians(latitude);
        longitude = Math.toRadians(longitude);

        // calculate great-circle distance
        double distance = Math.acos(Math.sin(destLat) * Math.sin(latitude) + Math.cos(destLat) * Math.cos(latitude) * Math.cos(destLng - longitude));

        // distance in human-readable format:
        // earth's radius in km = ~6371
        double distanceKM = 6371 * distance;
        return (String.format("%.2f" ,distanceKM));
    }


}
