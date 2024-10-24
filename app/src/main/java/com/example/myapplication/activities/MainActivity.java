package com.example.myapplication.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.adapters.HourlyAdapter;
import com.example.myapplication.domains.Hourly;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.PropertyPermission;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private final String API_KEY = "e528a17ddcd921a774f374911a5a1899";
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;
    private EditText cityInput;
    private TextView cityTxt, weatherTxt, timeTxt, tempTxt, cloudTxt, windTxt, humidityTxt, desTxt;
    private ImageView weatherImg;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String city = intent.getStringExtra("city");


        cityInput = findViewById(R.id.cityInput);
        cityTxt = findViewById(R.id.cityTxt);
        weatherTxt = findViewById(R.id.weatherTxt);
        timeTxt = findViewById(R.id.timeTxt);
        tempTxt = findViewById(R.id.tempTxt);
        cloudTxt = findViewById(R.id.cloudTxt);
        windTxt = findViewById(R.id.windTxt);
        humidityTxt = findViewById(R.id.humidityTxt);
        desTxt = findViewById(R.id.desTxt);
        weatherImg = findViewById(R.id.weatherImg);

        if(city != null) {
            getCurrentWeather(city);
            initRecyclerView(city);
            setVariable(city);
        }
        else{
            getCurrentWeather("hanoi");
            initRecyclerView("hanoi");
            setVariable("hanoi");
        }

        Button  searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = convertToEnglish(cityInput.getText().toString());

                if (!cityName.isEmpty()) {
                    getCurrentWeather(cityName);
                    initRecyclerView(cityName);
                    setVariable(cityName);
                    cityInput.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên thành phố", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getCurrentWeather(String city){
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+ city + ",vn&appid="+ API_KEY +"&units=metric&lang=en";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray weatherArray = response.getJSONArray("weather");
                            JSONObject weatherObj = weatherArray.getJSONObject(0);
                            JSONObject mainObj = response.getJSONObject("main");
                            JSONObject windObj = response.getJSONObject("wind");
                            JSONObject cloudObj = response.getJSONObject("clouds");

                            long timeDt = response.getLong("dt");
                            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeDt), ZoneId.systemDefault());
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMMM dd | hh:mm a");
                            timeTxt.setText(dateTime.format(formatter));

                            double tempDouble = mainObj.getDouble("temp");
                            int tempInt = (int) Math.round(tempDouble);

                            cityTxt.setText(response.getString("name"));
                            weatherTxt.setText(weatherObj.getString("main"));
                            tempTxt.setText(tempInt + "°C");
                            humidityTxt.setText(mainObj.getString("humidity") + "%");
                            windTxt.setText(windObj.getString("speed") + "m/s");
                            cloudTxt.setText(cloudObj.getString("all") + "%");
                            desTxt.setText(weatherObj.getString("description"));

                            switch (weatherObj.getString("main")){
                                case "Clouds":
                                    weatherImg.setImageResource(R.drawable.cloudy_sunny);
                                    break;
                                case "Rain":
                                case "Drizzle":
                                    weatherImg.setImageResource(R.drawable.rainy);
                                    break;
                                case "Thunderstorm":
                                    weatherImg.setImageResource(R.drawable.storm);
                                    break;
                                case "Snow":
                                    weatherImg.setImageResource(R.drawable.snowy);
                                    break;
                                case "Clear":
                                    weatherImg.setImageResource(R.drawable.sunny);
                                    break;
                                case "Squall":
                                case "Tornado":
                                    weatherImg.setImageResource(R.drawable.wind);
                                    break;
                                default:
                                    weatherImg.setImageResource(R.drawable.wind);
                                    break;
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void setVariable(String city) {
        TextView next7dayBtn = findViewById(R.id.nextBtn);
        next7dayBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FutureActivity.class);
            intent.putExtra("city", city);
            startActivity(intent);
        });
    }

    private void initRecyclerView(String city) {
        ArrayList<Hourly> items = new ArrayList<>();

        String url = "https://api.openweathermap.org/data/2.5/forecast?q="+ city + ",vn&appid="+ API_KEY +"&units=metric&lang=en";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray list = response.getJSONArray("list");
                            int index = 0;
                            while(true){
                                JSONObject itemObj = list.getJSONObject(index);
                                JSONObject mainObj = itemObj.getJSONObject("main");
                                JSONObject weatherObj = itemObj.getJSONArray("weather").getJSONObject(0);

                                long timeDt = itemObj.getLong("dt");
                                LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeDt), ZoneId.systemDefault());
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h a");

                                if (dateTime.getDayOfMonth() > LocalDateTime.now().getDayOfMonth()) {
                                    break;
                                }

                                double tempDouble = mainObj.getDouble("temp");
                                int tempInt = (int) Math.round(tempDouble);

                                String picPath = "";
                                switch (weatherObj.getString("main")){
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

                                items.add(new Hourly(dateTime.format(formatter), tempInt, picPath));
                                index++;

                            }

                            recyclerView = findViewById(R.id.view1);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));

                            adapterHourly = new HourlyAdapter(items);
                            recyclerView.setAdapter(adapterHourly);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);

    }

    private String convertToEnglish(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String noDiacritics = pattern.matcher(normalized).replaceAll("");
        return noDiacritics.replace(" ", "");
    }

}