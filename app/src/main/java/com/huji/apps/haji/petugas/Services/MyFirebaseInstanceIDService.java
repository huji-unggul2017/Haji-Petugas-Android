package com.huji.apps.haji.petugas.Services;

/**
 * Created by Dell_Cleva on 04/11/2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.huji.apps.haji.petugas.Utils.Config;
import com.huji.apps.haji.petugas.Utils.SessionManager;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONObject;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    AsyncHttpClient client = new AsyncHttpClient();

    SessionManager session;

    Boolean status;

    String id_users, refreshedToken;

    JSONObject doschedule;

    String id_user, msg, imgs, name, usertype, gradesel = "0";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        session = new SessionManager(getApplicationContext());

        session.createTokenFcm(refreshedToken);
        Log.d(TAG, refreshedToken);
        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }


}
