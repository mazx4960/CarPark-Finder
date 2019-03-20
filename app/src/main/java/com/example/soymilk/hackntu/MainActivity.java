package com.example.soymilk.hackntu;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.lang.*;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "FLAG";
    public static final String URL = "http://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2";


    AsyncHttpClient client;
    AsyncHttpClient oneMapHttpClient = new AsyncHttpClient();
    CarparkDatabase db;

    ProgressBar loadingCircle;

    // vars for calculating boundary
    double maxlat;
    double minlat;
    double maxlng;
    double minlng;

    boolean getDestFinish = false;

    TextView testView;

    ArrayList<String> searchSuggestions = new ArrayList<>();

    String realCoords = "";
    String destName;

    Button btnSearch;
    EditText searchTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(), CarparkDatabase.class, "CarparksDB").allowMainThreadQueries().build();
        client = new AsyncHttpClient();
        getAllCarpacks();
        //readFromDatabase(); //does not wait for the carparkdb to build first before calling

        btnSearch = (Button) findViewById(R.id.btnSearch);
        searchTerms = (EditText) findViewById(R.id.searchTerms);
        loadingCircle = (ProgressBar) findViewById(R.id.loading);

        searchTerms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 2) {
                    getLocationSuggestions(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destName = searchTerms.getText().toString();
                getLocationCoord(destName);

                loadingCircle.setVisibility(View.VISIBLE);






            }
        });
    }

    public void getLocationCoord(String searchTerm){
        RequestParams requestParams = new RequestParams();
        requestParams.put("searchVal", searchTerm);
        requestParams.put("returnGeom", "Y");
        requestParams.put("getAddrDetails", "Y");

        oneMapHttpClient.get("https://developers.onemap.sg/commonapi/search", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try{
                    JSONArray queries = response.getJSONArray("results");
                    JSONObject oneLocation = queries.getJSONObject(0);
                    String latitude = oneLocation.getString("LATITUDE");
                    String longtitude = oneLocation.getString("LONGTITUDE");
                    realCoords = latitude + " " + longtitude;
                    double[] doubleCoords = parseStringCoords(realCoords);
                    Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                    intent.putExtra("destCoords", doubleCoords);
                    intent.putExtra("destName", destName);
                    startActivity(intent);


                    /**** TESTING COMPLETE ****/
//                    TextView test = (TextView) findViewById(R.id.test);
//                    test.setText(realCoords.toString());

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void getLocationSuggestions(String searchTerm){

        // clear the array list of previous search suggestions
        searchSuggestions.clear();

        String BASE_URL = "https://developers.onemap.sg/commonapi/search";

        RequestParams requestParams = new RequestParams();

        requestParams.put("searchVal", searchTerm);
        requestParams.put("returnGeom", "Y");
        requestParams.put("getAddrDetails", "Y");

        oneMapHttpClient.get(BASE_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try{
                    parseOneMapJson(response);
//                    TextView testView = (TextView) findViewById(R.id.test);
//                    testView.setText(searchSuggestions.get(0));

                    // put the search results into an array adapter to show into the list view
                    ArrayAdapter<String> Adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1, searchSuggestions);
                    final ListView searchList = (ListView) findViewById(R.id.searchList);
                    searchList.setAdapter(Adapter);

                    searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            searchTerms.setText(searchSuggestions.get(position));
                        }
                    });

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void parseOneMapJson(JSONObject response) throws JSONException{
        JSONArray queries = response.getJSONArray("results");
        for (int i = 0; i < queries.length(); i++){
            JSONObject oneLocation = queries.getJSONObject(i);
            String place = oneLocation.getString("SEARCHVAL");
            searchSuggestions.add(place);
        }
    }

    public void getAllCarpacks(){
        RequestParams params = new RequestParams();
        client.addHeader("AccountKey", "GrGTvgRaRiuegLDJjKfKrw==");
        client.addHeader("accept", "application/json");
        client.get(URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try{
                    parseJsonResponse(response);
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
            double[] floatCoords = parseStringCoords(location);
            int availLots = singleValue.getInt("AvailableLots");
            String lotType = singleValue.getString("LotType");
            final Carpark newCarpark = new Carpark(carparkID, development, floatCoords[0], floatCoords[1], availLots, lotType);

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





    // returns float Coords (latitude in zeroth index and longitude in first index)

    private double[] parseStringCoords(String stringCoords){
        if(stringCoords!="" && stringCoords.contains(" ")){
            String[] coords = stringCoords.split(" ");
            double latitude = Double.parseDouble(coords[0]);
            double longitude = Double.parseDouble(coords[1]);
            Log.d(TAG, latitude + " " + longitude);
            double[] doubCoords = new double[]{latitude, longitude};
            return doubCoords;
        }else{
            return new double[]{0,0};
        }


    }

    /**** TEST METHOD ****/

    void readFromDatabase() {

        testView.setText(db.carpackDAO().getAll().get(0).lotType);


    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingCircle.setVisibility(View.GONE);
    }
}
