package com.example.timed;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.EditText;

public class NotificationActivity extends AppCompatActivity {

    EditText etMessage;
    EditText etTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        SharedPreferences sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        String message = sharedPreferences.getString("message", "");
        int time = sharedPreferences.getInt("time", 0);

        Log.d("Notification", "Shared Preferences Read: message="+message+" time="+time);

        etMessage = findViewById(R.id.et_message);
        etTime = findViewById(R.id.et_time);

        if (!message.equals("")) {
            etMessage.setText(message);
        }
        if (time != 0) {
            etTime.setText(String.valueOf(time));
        }

        findViewById(R.id.b_submit).setOnClickListener((view) -> handleSubmit());
    }

    private void handleSubmit() {
        SharedPreferences sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String message = etMessage.getText().toString();
        int time = Integer.parseInt(etTime.getText().toString());
        editor.putString("message", message);
        editor.putInt("time", time);
        editor.apply();
        Log.d("Notification","Saved to Shared Preferences: message="+message+" time="+time);
        scheduleAlarm(time);
    }

    private void scheduleAlarm(int time) {
        // https://developer.android.com/training/scheduling/alarms

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Log.d("Notification", "Unable to create AlarmManager object.");
            return;
        }

        // Cancel any alarm that is already set
        Intent oldAlarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent oldAlarmPendingIntent = PendingIntent.getBroadcast(this, 0, oldAlarmIntent, PendingIntent.FLAG_NO_CREATE);
        if (oldAlarmPendingIntent != null) {
            alarmManager.cancel(oldAlarmPendingIntent);
            oldAlarmPendingIntent.cancel();
            Log.d("Notification", "Canceled Previous Alarm");
        }

        if (time > 0) {
            // Create an alarm to go off every `time` milliseconds (inexact alarm)
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time*60000, time*60000, alarmPendingIntent);
            Log.d("Notification", "Alarm set for "+time+" sec");
        }
    }
}
