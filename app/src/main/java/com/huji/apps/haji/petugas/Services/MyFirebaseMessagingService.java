package com.huji.apps.haji.petugas.Services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.huji.apps.haji.petugas.BroadcastEmergencyActivity;
import com.huji.apps.haji.petugas.DetailBroadcastActivity;
import com.huji.apps.haji.petugas.HelpActivity;
import com.huji.apps.haji.petugas.HelpPetugasActivity;
import com.huji.apps.haji.petugas.MainActivity;
import com.huji.apps.haji.petugas.R;
import com.huji.apps.haji.petugas.Utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    Bitmap bitmap;
    String sImgPBC, sNamaBC, sTimeBC, sJudulBC, sIsiBC, sImageBC, sType;
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {

                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception P: " + e.getMessage());
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

//    final Uri alarmSound = Uri.parse(Reminder.getRingtone());

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json s1: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");
            String title = null, text = "Error", snama = "", simg = "", sid = "", slat = "", slon = "", sphone = "";
            String part_name = null, image = null, lats = null, lons = null,
                    phone = null, part_id = null, passport = null, codes = null;
            String[] bc_title, bc_message, imageUrl;
            int code = 0;
            if (data.has("code")) {
                code = data.getInt("code");
            }
            if (data.has("title")) {
                title = data.getString("title");
            }
            if (data.has("text")) {
                text = data.getString("text");
            }
            if (data.has("payload")) {
                JSONArray datak = data.getJSONArray("payload");
                JSONObject datas;

                if (code == 70) {
                    bc_title = new String[datak.length()];
                    bc_message = new String[datak.length()];
                    imageUrl = new String[datak.length()];
                    for (int i = 0; i < datak.length(); i++) {
                        datas = datak.getJSONObject(i);
                        bc_title[i] = datas.getString("bc_title");
                        bc_message[i] = datas.getString("bc_message");
                        imageUrl[i] = datas.getString("bc_image_200");
                        sImgPBC = datas.getString("part_image");
                        sNamaBC = datas.getString("part_fullname");
                        sTimeBC = datas.getString("timestamp");
                        sJudulBC = datas.getString("bc_title");
                        sIsiBC = datas.getString("bc_message");
                        sImageBC = datas.getString("bc_image_200");
                        sType = datas.getString("bc_type");
                    }
                    if (sType.equals("information")) {
                        if (!TextUtils.isEmpty(imageUrl[0])) {
                            if (imageUrl != null && imageUrl[0].length() > 4 && Patterns.WEB_URL.matcher(imageUrl[0]).matches()) {
                                try {
                                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl[0]).getContent());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (bitmap != null) {
                                    showBigNotification(bitmap, bc_title[0] + ", " + bc_message[0]);
                                } else {
                                    sendNotification(bc_title[0] + ", " + bc_message[0]);
                                }
                            }
                        } else {
                            sendNotification(bc_title[0] + ", " + bc_message[0]);
                        }
                    } else {
                        Intent intent = new Intent(this, BroadcastEmergencyActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("bcpimg", sImgPBC);
                        intent.putExtra("bcnama", sNamaBC);
                        intent.putExtra("bcwaktu", sTimeBC);
                        intent.putExtra("bcjudul", sJudulBC);
                        intent.putExtra("bcisi", sIsiBC);
                        intent.putExtra("bcimg", sImageBC);
                        startActivity(intent);
                    }
                } else if (code == 71) {
                    for (int i = 0; i < datak.length(); i++) {
                        datas = datak.getJSONObject(i);
                        snama = datas.getString("part_fullname");
                        simg = datas.getString("part_image");
                        sid = datas.getString("part_id");
                        slat = datas.getString("latitude");
                        slon = datas.getString("longitude");
                        sphone = datas.getString("part_callnum");
                    }

                    boolean run = getrun();
                    if (run) {
                        Log.w(TAG, "handleDataMessage: " +run + " Kirim" );
                        Intent pushNotifications = new Intent(Config.PUSH_NOTIFICATIONADA);
                        pushNotifications.putExtra("snama", snama);
                        pushNotifications.putExtra("simg", simg);
                        pushNotifications.putExtra("sid", sid);
                        pushNotifications.putExtra("lats", slat);
                        pushNotifications.putExtra("lons", slon);
                        pushNotifications.putExtra("sphone", sphone);
                        pushNotifications.putExtra("types", "tambah");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotifications);
                    } else {
                        Log.w(TAG, "handleDataMessage: " +run + " Open" );
                        Intent intent = new Intent(this, HelpActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("snama", snama);
                        intent.putExtra("simg", simg);
                        intent.putExtra("sid", sid);
                        intent.putExtra("lats", slat);
                        intent.putExtra("lons", slon);
                        intent.putExtra("sphone", sphone);
                        intent.putExtra("types", "tambah");
                        startActivity(intent);
                    }
                } else if (code == 73) {
                    for (int i = 0; i < datak.length(); i++) {
                        datas = datak.getJSONObject(i);
                        sid = datas.getString("part_id");
                    }

                    Log.w(TAG, "handleDataMessage: cancel dung"  +sid);
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("si", sid);
                    pushNotification.putExtra("typess", "cancel");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                }
                // PETUGAS HELP
                else if (code == 72){
                    for (int i = 0; i < datak.length(); i++) {
                        datas = datak.getJSONObject(i);
                        part_name = datas.getString("part_fullname");
                        image = datas.getString("part_image");
                        lats = datas.getString("latitude");
                        lons = datas.getString("longitude");
                        phone = datas.getString("part_callnum");
                        part_id = datas.getString("part_id");
                    }

                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("name", part_name);
                    pushNotification.putExtra("image", image);
                    pushNotification.putExtra("lat", lats);
                    pushNotification.putExtra("lon",lons);
                    pushNotification.putExtra("tel", phone);
                    pushNotification.putExtra("id", part_id);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    Log.w(TAG, "handleDataMessage: " + part_name + image+ lats+lons+phone);
                } else if (code == 74){

                    boolean runs = getruns();
                    Log.w(TAG, "handleDataMessage: " + runs );
                    if (runs) {
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION_BUZZ);
                        pushNotification.putExtra("do", "finish");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    } else {
                        Intent i = new Intent(this, HelpPetugasActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("bcpimg", sImgPBC);
                        i.putExtra("finish", "finish");
                        startActivity(i);
                        Log.e(TAG, "handleDataMessage: Help");
                    }
                } else if (code == 75) {
                    boolean runs = getruns();
                    Log.w(TAG, "handleDataMessage: " + runs );
                    if (runs) {
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION_BUZZ);
                        pushNotification.putExtra("do", "Buzz");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                        Log.w(TAG, "handleDataMessage: Do Buzz");
                    } else {
                        Intent i = new Intent(this, HelpPetugasActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("bcpimg", sImgPBC);
                        i.putExtra("do", "Buzz");
                        startActivity(i);
                        Log.e(TAG, "handleDataMessage: Help");
                    }
                } else if (code == -75) {
                    boolean runs = getruns();
                    Log.w(TAG, "handleDataMessage: " + runs );
                    if (runs) {
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION_BUZZ);
                        pushNotification.putExtra("do", "cancel");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    } else {

                        Intent i = new Intent(this, HelpPetugasActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("bcpimg", sImgPBC);
                        i.putExtra("do", "cancel");
                        startActivity(i);
                        Log.e(TAG, "handleDataMessage: Help");
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private boolean getrun() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> tasks = manager.getRunningAppProcesses();

        Log.e("current_app", tasks.get(0).processName);

        try {

            // Using ACTIVITY_SERVICE with getSystemService(String)
            // to retrieve a ActivityManager for interacting with the global system state.

            ActivityManager am = (ActivityManager) this
                    .getSystemService(Context.ACTIVITY_SERVICE);

            // Return a list of the tasks that are currently running,
            // with the most recent being first and older ones after in order.
            // Taken 1 inside getRunningTasks method means want to take only
            // top activity from stack and forgot the olders.

            List<ActivityManager.RunningTaskInfo> alltasks = am
                    .getRunningTasks(1);

            //
            for (ActivityManager.RunningTaskInfo aTask : alltasks) {

                // Used to check for CURRENT example main screen

                String packageName = "com.huji.apps.haji.petugas";

                // These are showing current running activity in logcat with
                // the use of different methods

                Log.i(TAG, "===============================");

                Log.i(TAG, "aTask.baseActivity: "
                        + aTask.baseActivity.flattenToShortString());

                Log.i(TAG, "aTask.baseActivity: "
                        + aTask.baseActivity.getClassName());

                Log.i(TAG, "aTask.topActivity: "
                        + aTask.topActivity.flattenToShortString());

                Log.i(TAG, "aTask.topActivity: "
                        + aTask.topActivity.getClassName());

                Log.i(TAG, "===============================");

                return aTask.topActivity.getClassName().equals(
                        packageName + ".HelpActivity");

            }
            return false;
        } catch (Throwable t) {
            Log.i(TAG, "Throwable caught: "
                    + t.getMessage(), t);
            return false;
        }
    }

    private boolean getruns() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> tasks = manager.getRunningAppProcesses();

        Log.e("current_app", tasks.get(0).processName);

        try {

            // Using ACTIVITY_SERVICE with getSystemService(String)
            // to retrieve a ActivityManager for interacting with the global system state.

            ActivityManager am = (ActivityManager) this
                    .getSystemService(Context.ACTIVITY_SERVICE);

            // Return a list of the tasks that are currently running,
            // with the most recent being first and older ones after in order.
            // Taken 1 inside getRunningTasks method means want to take only
            // top activity from stack and forgot the olders.

            List<ActivityManager.RunningTaskInfo> alltasks = am
                    .getRunningTasks(1);

            //
            for (ActivityManager.RunningTaskInfo aTask : alltasks) {

                // Used to check for CURRENT example main screen

                String packageName = "com.huji.apps.haji.petugas";

                // These are showing current running activity in logcat with
                // the use of different methods

                Log.i(TAG, "===============================");

                Log.i(TAG, "aTask.baseActivity: "
                        + aTask.baseActivity.flattenToShortString());

                Log.i(TAG, "aTask.baseActivity: "
                        + aTask.baseActivity.getClassName());

                Log.i(TAG, "aTask.topActivity: "
                        + aTask.topActivity.flattenToShortString());

                Log.i(TAG, "aTask.topActivity: "
                        + aTask.topActivity.getClassName());

                Log.i(TAG, "===============================");

                return aTask.topActivity.getClassName().equals(
                        packageName + ".HelpPetugasActivity");

            }
            return false;
        } catch (Throwable t) {
            Log.i(TAG, "Throwable caught: "
                    + t.getMessage(), t);
            return false;
        }
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                Log.w(TAG, "isAppIsInBackground: " + processInfo.getClass().getSimpleName());
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            Log.w(TAG, "isAppIsInBackground: " + componentInfo.getClassName() );
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    private void showBigNotification(Bitmap bitmap, String message) {

        Intent intent = new Intent(this, DetailBroadcastActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("bcpimg", sImgPBC);
        intent.putExtra("bcnama", sNamaBC);
        intent.putExtra("bcwaktu", sTimeBC);
        intent.putExtra("bcjudul", sJudulBC);
        intent.putExtra("bcisi", sIsiBC);
        intent.putExtra("bcimg", sImageBC);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(getString(R.string.app_name));
        bigPictureStyle.setSummaryText(message);
        bigPictureStyle.bigPicture(bitmap);

        Notification notification;
        notification = mBuilder.setTicker(getString(R.string.app_name)).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m /* ID of notification */, notification);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setTicker(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m /* ID of notification */, notificationBuilder.build());
    }
}
