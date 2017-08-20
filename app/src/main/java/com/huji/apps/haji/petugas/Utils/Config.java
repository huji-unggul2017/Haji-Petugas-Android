package com.huji.apps.haji.petugas.Utils;

/**
 * Created by Dell_Cleva on 04/11/2016.
 */

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String PUSH_NOTIFICATIONADA = "pushNotificationAda";
    public static final String PUSH_NOTIFICATION_BUZZ = "pushNotificationBuzz";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "huji_petugas_firebase";
}