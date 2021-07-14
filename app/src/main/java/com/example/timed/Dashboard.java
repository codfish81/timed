package com.example.timed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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


        /*// data to populate the RecyclerView with
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
        appTime.add("30 min");*/

        AppDataManager usageMgr = new AppDataManager(this);
        List<AppDataManager.AppUsage> usageList = usageMgr.getUsage(AppDataManager.DAY_MS);

        // total time
//        int total = usageMgr.getTotalUsage(AppDataManager.DAY_MS);
//        TextView totalTime = (TextView) findViewById(R.id.total_usage);
//        totalTime.setText(total);

        ArrayList<String> appName = new ArrayList<>();
        ArrayList<String> appTime = new ArrayList<>();
//        ArrayList<ClipData.Item> appIcon = new ArrayList<>();

        for(AppDataManager.AppUsage usage : usageList)
        {
            appName.add(usage.name);
            appTime.add(DateUtils.formatElapsedTime(usage.usageMs / 1000));

            /*try
            {
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                Drawable icon = Dashboard.this.getPackageManager().getApplicationIcon(usage.name);
                imageView.setImageDrawable(icon);
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }*/
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvToday);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, appName, appTime);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


    }

    public void onItemClick(final View view, int position) {
        // setContentView(R.layout.recyclerview_row);
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(view.getContext(), PerAppView.class);
        view.getContext().startActivity(intent);
    }


    public static void getAppData(String[] args) {
    }

    public static void showAppData(String[] args) {

    }

}