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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private final String API_KEY = "e528a17ddcd921a774f374911a5a1899";
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;
    private EditText cityInput;
    private TextView cityTxt, weatherTxt, timeTxt, tempTxt, cloudTxt, windTxt, humidityTxt, desTxt;
    private Button searchBtn;
    private ImageView weatherImg;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();
        setVariable();
        cityInput = findViewById(R.id.cityInput);
        cityTxt = findViewById(R.id.cityTxt);
        weatherTxt = findViewById(R.id.weatherTxt);
        timeTxt = findViewById(R.id.timeTxt);
        tempTxt = findViewById(R.id.tempTxt);
        cloudTxt = findViewById(R.id.cloudTxt);
        windTxt = findViewById(R.id.windTxt);
        humidityTxt = findViewById(R.id.humidityTxt);
        desTxt = findViewById(R.id.desTxt);
        searchBtn = findViewById(R.id.searchBtn);
        weatherImg = findViewById(R.id.weatherImg);


        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMMM dd | hh:mm a", Locale.ENGLISH);
        String formattedDateTime = currentDateTime.format(formatter);
        timeTxt.setText(formattedDateTime);

        getCurrentWeather("hanoi");

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = convertToEnglish(cityInput.getText().toString());

                if (!cityName.isEmpty()) {
                    getCurrentWeather(cityName);
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên thành phố", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getCurrentWeather(String city){
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+ city + "&appid="+ API_KEY +"&units=metric&lang=vi";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray weatherArray = response.getJSONArray("weather");
                            JSONObject weatherObj = weatherArray.getJSONObject(0);
                            JSONObject mainObj = response.getJSONObject("main");
                            JSONObject windObj = response.getJSONObject("wind");
                            JSONObject cloudObj = response.getJSONObject("clouds");

                            double tempDouble = mainObj.getDouble("temp");
                            int tempInt = (int) Math.round(tempDouble);

                            cityTxt.setText(response.getString("name"));
                            weatherTxt.setText(weatherObj.getString("main"));
                            tempTxt.setText(tempInt + "°C");
                            humidityTxt.setText(mainObj.getString("humidity") + "%");
                            windTxt.setText(windObj.getString("deg") + "%");
                            cloudTxt.setText(cloudObj.getString("all") + "%");
                            desTxt.setText(weatherObj.getString("description"));

                            switch (weatherObj.getString("main")){
                                case "Clouds":
                                    weatherImg.setImageResource(R.drawable.cloudy_sunny);
                                    break;
                                case "Rain":
                                case "Drizzle":
                                case "Thunderstorm":
                                    weatherImg.setImageResource(R.drawable.rainy);
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

    private void setVariable() {
        TextView next7dayBtn = findViewById(R.id.nextBtn);
        next7dayBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, FutureActivity.class)));
    }

    private void initRecyclerView() {
        ArrayList<Hourly> items = new ArrayList<>();

        items.add(new Hourly("9 pm", 28, "cloudy"));
        items.add(new Hourly("10 pm", 29, "sunny"));
        items.add(new Hourly("11 pm", 30, "windy"));
        items.add(new Hourly("12 pm", 31, "rain"));
        items.add(new Hourly("1 pm", 32, "storm"));

        recyclerView = findViewById(R.id.view1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterHourly = new HourlyAdapter(items);
        recyclerView.setAdapter(adapterHourly);
    }
    private String convertToEnglish(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String noDiacritics = pattern.matcher(normalized).replaceAll("");
        return noDiacritics.replace(" ", "");
    }

}