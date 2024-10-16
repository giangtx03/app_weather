package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.FutureAdapter;
import com.example.myapplication.domains.FutureDomain;

import java.util.ArrayList;

public class FutureActivity extends AppCompatActivity {


    private RecyclerView.Adapter adapterTomorrow;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future);

        setVariable();
        initRecyclerView();
    }

    private void initRecyclerView() {
        ArrayList<FutureDomain> items = new ArrayList<>();
        items.add(new FutureDomain("Sat", "storm", "Storm", 24,12));
        items.add(new FutureDomain("Sun", "cloudy", "Cloudy", 25,16));
        items.add(new FutureDomain("Mon", "windy", "Windy", 29,15));
        items.add(new FutureDomain("Tue", "cloudy_sunny", "Cloudy Sunny", 23,15));
        items.add(new FutureDomain("Wed", "sunny", "Sunny", 28,11));
        items.add(new FutureDomain("Thu", "rainy", "Rainy", 23,12));

        recyclerView = findViewById(R.id.view2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapterTomorrow = new FutureAdapter(items);
        recyclerView.setAdapter(adapterTomorrow);
    }

    private void setVariable() {
        ConstraintLayout backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> startActivity(new Intent(FutureActivity.this, MainActivity.class)));
    }
}