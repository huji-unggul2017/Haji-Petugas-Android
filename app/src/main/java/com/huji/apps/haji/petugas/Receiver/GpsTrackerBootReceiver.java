package com.huji.apps.haji.petugas.Receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.huji.apps.haji.petugas.Utils.Constant;


public class GpsTrackerBootReceiver extends BroadcastReceiver {
    private static final String TAG = "GpsTrackerBootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive Boot/Killer: Start");
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.PrefTAG, Context.MODE_PRIVATE);
        int intervalInMinutes = sharedPreferences.getInt("intervalInMinutes", 1);
        Boolean currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", true);

        if (currentlyTracking) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    intervalInMinutes * 20000, // 60000 = 1 minute,
                    pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }

//        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            Toast.makeText(context, "I am Running", Toast.LENGTH_SHORT).show();
//        }
    }
}
