package com.example.timed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WeekView extends AppCompatActivity {

    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);

        // set a toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // radio button
        RadioGroup toggle = (RadioGroup) findViewById(R.id.toggle);
        RadioButton rd1 = findViewById(R.id.radioWeek);
        RadioButton rd2 = findViewById(R.id.radioToday);
        toggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rd2.isChecked()) {
                    Intent intent = new Intent(getApplication(), Dashboard.class);
                    startActivity(intent);
                }
            }
        });

        // date
        TextView totalTime = (TextView) findViewById(R.id.date);
        totalTime.setText("Tue, Jun 8");


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

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvWeek);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, appName, appTime);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return super.onSupportNavigateUp();
    }
}
