package com.huji.apps.haji.petugas.Utils;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

/**
 * Created by Dell_Cleva on 13/03/2017.
 */

public class MyApps extends Application {

    private static final String TAG = "MyApps";
    private static MyApps mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(TAG, "onCreate: APPS" );
        mInstance = this;
    }

    public static synchronized MyApps getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
