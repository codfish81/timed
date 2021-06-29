package com.example.timed;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PerAppView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        // set a toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set a back button in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        // date
        TextView totalTime = (TextView) findViewById(R.id.date);
        totalTime.setText("Tue, Jun 8");

        // app name
        TextView appName = (TextView) findViewById(R.id.appName);
        appName.setText("Spotify");

        // app usage
        TextView appTime = (TextView) findViewById(R.id.appTime);
        appTime.setText("1 hr, 21 min / 2 hr");

        // app timer
        TextView timer = (TextView) findViewById(R.id.timer);
        timer.setText("2h");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return super.onSupportNavigateUp();
    }
}
