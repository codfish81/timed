package com.example.timed;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WeekView extends AppCompatActivity {

    /*
     * Weekly View
     * sets the app list (recycler view) for the past few weeks and charts
     */

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

        // Gets data through AppDataManager
        AppDataManager usageMgr = new AppDataManager(this);
        List<AppDataManager.AppUsage> usageList = usageMgr.getUsageForWeek();
        ArrayList<String> appName = new ArrayList<>();
        ArrayList<String> appTime = new ArrayList<>();
        ArrayList<Drawable> appIcon = new ArrayList<>();

        // adds data to the list
        for(AppDataManager.AppUsage usage : usageList)
        {
            appName.add(usage.name);
            appTime.add(DateUtils.formatElapsedTime(usage.usageMs / 1000));
            appIcon.add(usage.icon);
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvWeek);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, appName, appTime, appIcon);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    // set a back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return super.onSupportNavigateUp();
    }
}
