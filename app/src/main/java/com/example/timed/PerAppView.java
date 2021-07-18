package com.example.timed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class PerAppView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        Intent intent = getIntent();
        String APPNAME = intent.getStringExtra("APP_NAME");
        String APPTIME = intent.getStringExtra("APP_TIME");
        // Drawable APPICON = getResources().getDrawable(intent.getIntExtra("APP_ICON",-1));

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
        TextView name = (TextView) findViewById(R.id.appName);
        name.setText(APPNAME);

        // app icon
//        ImageView icon = (ImageView) findViewById(R.id.icon);
//        icon.setImageDrawable(APPICON);

        // app usage
        TextView appTime = (TextView) findViewById(R.id.appTime);
        appTime.setText(APPTIME);

        // app timer

    }

    public void onItemPresss(View view) {
        Intent intent = new Intent(this, NotificationActivity.class);
//        intent.putExtra("APP_NAME", mData.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return super.onSupportNavigateUp();
    }
}
