package com.example.timed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // set a tool bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // radio button
        RadioGroup toggle = (RadioGroup) findViewById(R.id.toggle);
        RadioButton rd1 = findViewById(R.id.radioWeek);
        RadioButton rd2 = findViewById(R.id.radioToday);
        toggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rd1.isChecked()) {
                    Intent intent = new Intent(getApplication(), WeekView.class);
                    startActivity(intent);
                }
            }
        });


        // total time
        TextView totalTime = (TextView) findViewById(R.id.totalTime);
        totalTime.setText("2 hr, 12 min");


        // data to populate the RecyclerView with
        ArrayList<String> appName = new ArrayList<>();
        appName.add("Spotify");
        appName.add("Chrome");
        appName.add("Twitter");
        appName.add("Instagram");
        appName.add("Tiktok");
        appName.add("Facebook");
        appName.add("Kindle");
        appName.add("Pinterest");

        ArrayList<String> appTime = new ArrayList<>();
        appTime.add("1 hr,　21 min");
        appTime.add("38 min");
        appTime.add("20 min");
        appTime.add("18 min");
        appTime.add("15 min");
        appTime.add("12 min");
        appTime.add("10 min");
        appTime.add("8 min");

        ArrayList<String> timeLimit = new ArrayList<>();
        appTime.add("2 hr");
        appTime.add("2 hr");
        appTime.add("1 hr");
        appTime.add("30 min");
        appTime.add("30 min");
        appTime.add("30 min");
        appTime.add("30 min");
        appTime.add("30 min");


        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvToday);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, appName, appTime);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplication(), PerAppView.class);
        startActivity(intent);
        finish();
    }


    public static void getAppData(String[] args) {
    }

    public static void showAppData(String[] args) {

    }

}