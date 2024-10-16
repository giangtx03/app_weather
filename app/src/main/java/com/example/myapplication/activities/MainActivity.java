package com.example.myapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.HourlyAdapter;
import com.example.myapplication.domains.Hourly;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();
        setVariable();
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
}