package com.huji.apps.haji.petugas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.huji.apps.haji.petugas.Utils.ConnectivityReceiver;
import com.huji.apps.haji.petugas.Utils.Constant;
import com.huji.apps.haji.petugas.Utils.LoopjHttpClient;
import com.huji.apps.haji.petugas.Utils.MyApps;
import com.huji.apps.haji.petugas.Utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class SplashActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener,
        ResultCallback<LocationSettingsResult>, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = "SplashActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    protected GoogleApiClient mGoogleApiClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;
    SessionManager sessionManager;
    LinearLayout lno;
    boolean help_state;
    JSONObject jresponse = new JSONObject(), jdata = new JSONObject(), jhelpstate = new JSONObject();
    String part_id, part_fullname, tokens = null, part_phone, sEmail, sIMEI;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(this);
        sIMEI = sessionManager.getImei();
        lno = (LinearLayout) findViewById(R.id.lnokonek);

        checkConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApps.getInstance().setConnectivityListener(SplashActivity.this);
    }

    public void Recreate(View view) {
        Log.e(TAG, "Recreate: Klick");
        checkConnection();
        if (ConnectivityReceiver.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void TOMain() {
        Log.e(TAG, "ToState " + help_state);
        sessionManager.CreateisHelp(help_state);
        new android.os.Handler().postDelayed(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 100);
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    public void showSnack(boolean isConnected) {
        if (isConnected) {
            lno.setVisibility(View.GONE);

            if (checkPlayServices()) {
                // Kick off the process of building the GoogleApiClient, LocationRequest, and
                // LocationSettingsRequest objects.
                buildGoogleApiClient();
                createLocationRequest();
                buildLocationSettingsRequest();
                checkLocationSettings();

            }
        } else {
            lno.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ConnectivityReceiver.isConnected()) {
            if (checkPlayServices()) {
                if (mGoogleApiClient != null)
                    mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    public void CheckDataLogin() {

        sEmail = sessionManager.getEmails();
        RequestParams requestParams = new RequestParams();
        requestParams.put("passport", sessionManager.getPassport());
        requestParams.put("pin", sessionManager.getPin());
        requestParams.put("app_type", Constant.APP_TYPE);
        requestParams.put("imei", sIMEI);
        StringEntity entity = null;


        try {
            entity = new StringEntity(requestParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(SplashActivity.this, Constant.InitPIN, entity, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int kode = 0;
                String kuid, rom, group;
                try {
                    jresponse = response.getJSONObject("response");
                    if (jresponse.has("access_token")) {
                        tokens = jresponse.getString("access_token");
                    }
                    kode = jresponse.getInt("code");
                    if (jresponse.has("data")) {
                        jdata = jresponse.getJSONObject("data");
                        Log.e(TAG, "onSuccess: Data " + String.valueOf(jdata.toString()));
                        part_id = jdata.getString("part_id");
                        part_fullname = jdata.getString("part_fullname");
                        sessionManager.createLoginSession(jdata.getString("part_passport"), part_fullname, part_id, jdata.getString("part_country_code") + jdata.getString("part_callnum"), tokens, sessionManager.getPin(), jdata.getString("part_type"));

                        if (!jdata.isNull("kuid")) {
                            kuid = jdata.getString("kuid");
                            sessionManager.CreateisKuid(kuid);
                        } else {
                            sessionManager.CreateisKuid("0");
                        }

                        if (!jdata.isNull("rom_id")) {
                            rom = jdata.getString("rom_id");
                            sessionManager.CreateisRom(rom);
                        } else {
                            sessionManager.CreateisRom("0");
                        }

                        if (!jdata.isNull("group_id")) {
                            group = jdata.getString("group_id");
                            sessionManager.CreateisGroup(group);
                        } else {
                            sessionManager.CreateisGroup("0");
                        }
                    }
                    if (jresponse.has("help_state")) {
                        jhelpstate = jresponse.getJSONObject("help_state");
                        help_state = jhelpstate.getBoolean("ishelp");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "onSuccess: CODE SPLASH " +String.valueOf(kode) );
                if (kode == 100) {
                    TOMain();

                } else if (kode == 501) {
                    new android.os.Handler().postDelayed(new Runnable() {
                        @SuppressLint("NewApi")
                        @Override
                        public void run() {
                            sessionManager.logoutUser();
                            finish();
                        }
                    }, 500);
                } else if (kode == 104){
                    new android.os.Handler().postDelayed(new Runnable() {
                        @SuppressLint("NewApi")
                        @Override
                        public void run() {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashActivity.this);
                            builder.setTitle("Informasi");
                            builder.setMessage("Tidak dapat memverifikasi akun Anda. Otomatis keluar. Silahkan login kembali");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    sessionManager.logoutUser();
                                    finish();
                                }
                            }).show();
                        }
                    }, 500);
//                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashActivity.this);
//                    builder.setTitle("Informasi");
//                    builder.setMessage("Anda telah login di perangkat lain. Apakah Anda ingin keluar dari perangkat tersebut?");
//                    builder.setCancelable(false);
//                    builder.setNegativeButton("Tidak", new
//                            DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int arg1) {
//                                    // TODO Auto-generated method stub
//                                    dialog.cancel();
//                                    finish();
//                                }
//                            });
//                    builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            UpdateIMEI(tokens);
//                        }
//                    }).show();
                }
                Log.d(TAG, "onSuccess: " + String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "onFailure: " + responseString);
                lno.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "onFailure: " + throwable.toString());
                Log.i(TAG, "onFailure: Muncul dong");
                lno.setVisibility(View.VISIBLE);
            }
        });
    }

    public void UpdateIMEI(String acces) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("imei", sIMEI);
        requestParams.put("app_type", Constant.APP_TYPE);
        StringEntity entity = null;

        try {
            entity = new StringEntity(requestParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(this, Constant.UpdateImei + acces, entity, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int kode = 0;

                try {
                    jresponse = response.getJSONObject("response");
                    kode = jresponse.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (kode == 0) {
                    new android.os.Handler().postDelayed(new Runnable() {
                        @SuppressLint("NewApi")
                        @Override
                        public void run() {
                            TOMain();
                        }
                    }, 500);
                }
                Log.d(TAG, "onSuccess: " + String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(SplashActivity.this, "Terjadi kesalahan, silahkan coba lagi. . .", Toast.LENGTH_SHORT).show();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Informasi");
                builder.setMessage("Anda telah login di perangkat lain. Apakah Anda ingin keluar dari perangkat tersebut?");
                builder.setCancelable(false);
                builder.setNegativeButton("Tidak", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                // TODO Auto-generated method stub
                                dialog.cancel();
                                finish();
                            }
                        });
                builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UpdateIMEI(tokens);
                    }
                }).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "onFailure: " + throwable.toString());
                Log.i(TAG, "onFailure: Muncul dong");
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Informasi");
                builder.setMessage("Anda telah login di perangkat lain. Apakah Anda ingin keluar dari perangkat tersebut?");
                builder.setCancelable(false);
                builder.setNegativeButton("Tidak", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                // TODO Auto-generated method stub
                                dialog.cancel();
                                finish();
                            }
                        });
                builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UpdateIMEI(tokens);
                    }
                }).show();
            }
        });
    }

    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        Log.e(TAG, "buildLocationSettingsRequest: Masup");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        Log.e(TAG, "checkLocationSettings: Masup juga");
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.e(TAG, "All location settings are satisfied.");
                // disini
                CheckDataLogin();
//                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.e(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Disini
                        CheckDataLogin();
//                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        checkLocationSettings();
                        break;
                }
                break;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

//        mLocationRequest.setSmallestDisplacement(10);
    }

//    /**
//     * Requests location updates from the FusedLocationApi.
//     */
//    protected void startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient,
//                mLocationRequest,
//                this
//        ).setResultCallback(new ResultCallback<Status>() {
//            @Override
//            public void onResult(Status status) {
////                mRequestingLocationUpdates = true;
//            }
//        });
//    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
//        showSnack(isConnected);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        if (checkPlayServices()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        startLocationUpdates();
        Log.i(TAG, "onConnected: ");
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}