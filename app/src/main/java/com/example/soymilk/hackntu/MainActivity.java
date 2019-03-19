package com.example.soymilk.hackntu;

import android.arch.persistence.room.Room;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "FLAG";
    public static final String URL = "http://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2";
    AsyncHttpClient client;
    CarparkDatabase db;

    // vars for calculating boundary
    float maxlat;
    float minlat;
    float maxlng;
    float minlng;

    TextView testView;

    // TODO: Get REAL coords
    String dummyDestCoords = "1.29375 103.85718";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testView = (TextView)findViewById(R.id.textView);

        db = Room.databaseBuilder(getApplicationContext(), CarparkDatabase.class, "CarparksDB").build();
        client = new AsyncHttpClient();
        parseStringCoords(dummyDestCoords);
        getAllCarpacks();


    }

    public void getAllCarpacks(){
        RequestParams params = new RequestParams();
        client.addHeader("AccountKey", getString(R.string.key));
        client.addHeader("accept", "application/json");
        client.get(URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try{
                    parseJsonResponse(response);
                    testView.setText(response.getJSONArray("value").getJSONObject(0).getString("Development"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void parseJsonResponse(JSONObject response) throws JSONException {
        final ArrayList<Carpark> listOfCarparks = new ArrayList<>();
        JSONArray values = response.getJSONArray("value");
        for(int i = 0; i < values.length(); i++){

            JSONObject singleValue = values.getJSONObject(i);
            String carparkID = singleValue.getString("CarParkID");
            String development = singleValue.getString("Development");
            String location = singleValue.getString("Location");
            int availLots = singleValue.getInt("AvailableLots");
            String lotType = singleValue.getString("LotType");
            final Carpark newCarpark = new Carpark(carparkID, development, location, availLots, lotType);

            AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    db.carpackDAO().insertAll(newCarpark);
                    return null;
                }
            };
            task.execute();

        }



    }

    private void calcBoundaries(String destCoords){




    }

    private void getNearybyCarparks(int radius){

    }

    // returns float Coords (latitude in zeroth index and longitude in first index)

    private float[] parseStringCoords(String stringCoords){
        String[] coords = stringCoords.split(" ");
        float latitude = Float.parseFloat(coords[0]);
        float longitude = Float.parseFloat(coords[1]);
        Log.d(TAG, latitude + " " + longitude);
        float[] floatCoords = new float[]{latitude, longitude};
        return floatCoords;
    }




}
