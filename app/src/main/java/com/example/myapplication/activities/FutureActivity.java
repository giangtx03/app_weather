package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.adapters.FutureAdapter;
import com.example.myapplication.domains.FutureDomain;
import com.example.myapplication.domains.Hourly;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class FutureActivity extends AppCompatActivity {
    private final String API_KEY = "e528a17ddcd921a774f374911a5a1899";
    private RecyclerView.Adapter adapterTomorrow;
    private RecyclerView recyclerView;
    private String city;
    private TextView tempTomTxt, weatherTomTxt, cloudTxt, windTxt, humidityTxt;
    private ImageView pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future);

        Intent intent = getIntent();
        city = intent.getStringExtra("city");

        tempTomTxt = findViewById(R.id.tempTomTxt);
        weatherTomTxt = findViewById(R.id.weatherTomTxt);
        cloudTxt = findViewById(R.id.cloudTxt);
        windTxt = findViewById(R.id.windTxt);
        humidityTxt = findViewById(R.id.humidityTxt);
        pic = findViewById(R.id.pic);

        setVariable();
        initRecyclerView();
    }


    private void initRecyclerView() {
        ArrayList<FutureDomain> items = new ArrayList<>();

        String url = "https://api.openweathermap.org/data/2.5/forecast?q="+ city + ",vn&appid="+ API_KEY +"&units=metric&lang=en";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray list = response.getJSONArray("list");
                            HashSet<String> processedDates = new HashSet<>();
                            JSONObject forecast0 = list.getJSONObject(0);
                            String dtTxt0 = forecast0.getString("dt_txt");
                            String dateOnly0 = dtTxt0.split(" ")[0];

                            processedDates.add(dateOnly0);

                            JSONObject mainObj = forecast0.getJSONObject("main");
                            JSONObject weatherObj = forecast0.getJSONArray("weather").getJSONObject(0);
                            JSONObject windObj = forecast0.getJSONObject("wind");
                            JSONObject cloudObj = forecast0.getJSONObject("clouds");

                            double tempTom = mainObj.getDouble("temp");
                            int tempTomInt = (int) Math.round(tempTom);

                            tempTomTxt.setText(String.valueOf(tempTomInt) + "°C");
                            weatherTomTxt.setText(weatherObj.getString("main"));
                            cloudTxt.setText(cloudObj.getString("all") + "%");
                            humidityTxt.setText(mainObj.getString("humidity") + "%");
                            windTxt.setText(windObj.getString("speed") + "m/s");

                            switch (weatherObj.getString("main")){
                                case "Clouds":
                                    pic.setImageResource(R.drawable.cloudy_sunny);
                                    break;
                                case "Rain":
                                case "Drizzle":
                                    pic.setImageResource(R.drawable.rainy);
                                    break;
                                case "Thunderstorm":
                                    pic.setImageResource(R.drawable.storm);
                                    break;
                                case "Snow":
                                    pic.setImageResource(R.drawable.snowy);
                                    break;
                                case "Clear":
                                    pic.setImageResource(R.drawable.sunny);
                                    break;
                                case "Squall":
                                case "Tornado":
                                    pic.setImageResource(R.drawable.wind);
                                    break;
                                default:
                                    pic.setImageResource(R.drawable.wind);
                                    break;
                            }

                            for (int i = 0; i < list.length(); i++) {
                                JSONObject forecast = list.getJSONObject(i);
                                String dtTxt = forecast.getString("dt_txt");
                                String dateOnly = dtTxt.split(" ")[0];


                                if (!processedDates.contains(dateOnly)) {
                                    processedDates.add(dateOnly);

                                    JSONObject main = forecast.getJSONObject("main");
                                    JSONObject weather = forecast.getJSONArray("weather").getJSONObject(0);
                                    long dt = forecast.getLong("dt");
                                    LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt), ZoneId.systemDefault());
                                    String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("en")).substring(0, 3);

                                    String picPath = "";

                                    switch (weather.getString("main")){
                                        case "Clouds":
                                            picPath = "cloudy_sunny";
                                            break;
                                        case "Rain":
                                        case "Drizzle":
                                            picPath = "rain";
                                            break;
                                        case "Thunderstorm":
                                            picPath = "storm";
                                            break;
                                        case "Snow":
                                            picPath = "snowy";
                                            break;
                                        case "Clear":
                                            picPath = "sunny";
                                            break;
                                        case "Squall":
                                        case "Tornado":
                                            picPath = "windy";
                                            break;
                                        default:
                                            picPath = "cloudy";
                                            break;
                                    }

                                    int tempMin = Integer.MAX_VALUE;
                                    int tempMax = Integer.MIN_VALUE;

                                    for(int j = i; j < list.length(); j++){
                                        JSONObject fr = list.getJSONObject(j);
                                        String dtTxtTmp = fr.getString("dt_txt");
                                        String dateOnlyTmp = dtTxtTmp.split(" ")[0];
                                        if(dateOnly.equals(dateOnlyTmp)){
                                            tempMax = Math.max(tempMax, fr.getJSONObject("main").getInt("temp_max"));
                                            tempMin = Math.min(tempMin, fr.getJSONObject("main").getInt("temp_min"));
                                        }
                                        else{
                                            break;
                                        }
                                    }

                                    items.add(new FutureDomain(dayOfWeek, picPath, weather.getString("main"), tempMin,tempMax));
                                }
                            }


                            recyclerView = findViewById(R.id.view2);
                            recyclerView.setLayoutManager(new LinearLayoutManager(FutureActivity.this, LinearLayoutManager.VERTICAL, false));

                            adapterTomorrow = new FutureAdapter(items);
                            recyclerView.setAdapter(adapterTomorrow);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FutureActivity.this, "" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
//        items.add(new FutureDomain("Sat", "storm", "Storm", 24,12));
//        items.add(new FutureDomain("Sun", "cloudy", "Cloudy", 25,16));
//        items.add(new FutureDomain("Mon", "windy", "Windy", 29,15));
//        items.add(new FutureDomain("Tue", "cloudy_sunny", "Cloudy Sunny", 23,15));
//        items.add(new FutureDomain("Wed", "sunny", "Sunny", 28,11));
//        items.add(new FutureDomain("Thu", "rainy", "Rainy", 23,12));
//
//        recyclerView = findViewById(R.id.view2);
//        recyclerView.setLayoutManager(new LinearLayoutManager(FutureActivity.this, LinearLayoutManager.VERTICAL, false));
//
//        adapterTomorrow = new FutureAdapter(items);
//        recyclerView.setAdapter(adapterTomorrow);
    }

    private void setVariable() {
        ConstraintLayout backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(FutureActivity.this, MainActivity.class);
            intent.putExtra("city", city);
            startActivity(intent);
        });
    }
}