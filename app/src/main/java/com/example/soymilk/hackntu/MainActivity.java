package com.example.soymilk.hackntu;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.room.Room;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public static final String URL = "http://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2";
    AsyncHttpClient client;
    CarparkDatabase db = Room.databaseBuilder(getApplicationContext(), CarparkDatabase.class, "CarparksDB").build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new AsyncHttpClient();

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
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void parseJsonResponse(JSONObject response) throws JSONException {
        JSONArray values = response.getJSONArray("value");
        for(int i = 0; i < values.length(); i++){

            JSONObject singleValue = values.getJSONObject(i);
            int carparkID = Integer.parseInt(singleValue.getString("CarparkID"));
            String development = singleValue.getString("Development");
            String location = singleValue.getString("Location");
            int availLots = singleValue.getInt("AvailableLots");
            String lotType = singleValue.getString("LotType");
            Carpark newCarpark = new Carpark(carparkID, development, location, availLots, lotType);
            db.carpackDAO().insertAll(newCarpark);

        }

    }


}
