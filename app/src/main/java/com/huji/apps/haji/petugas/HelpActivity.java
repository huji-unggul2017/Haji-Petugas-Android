package com.huji.apps.haji.petugas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.huji.apps.haji.petugas.Beans.RowItemHelp;
import com.huji.apps.haji.petugas.Utils.Config;
import com.huji.apps.haji.petugas.Utils.ConnectivityReceiver;
import com.huji.apps.haji.petugas.Utils.Constant;
import com.huji.apps.haji.petugas.Utils.LatLngInterpolator;
import com.huji.apps.haji.petugas.Utils.LoopjHttpClient;
import com.huji.apps.haji.petugas.Utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class HelpActivity extends AppCompatActivity implements OnMapReadyCallback, ViewTreeObserver.OnGlobalLayoutListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        ResultCallback<LocationSettingsResult>{

    private static final String TAG = "HelpActivity";
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

    public BroadcastReceiver mRegistrationBroadcastReceiver;
    protected PowerManager.WakeLock mWakeLock;
    int dot = 200; // Length of a Morse Code "dot" in milliseconds
    int short_gap = 0; // Length of Gap Between dots/dashes
    long[] pattern = {0, // Start immediately
            dot, short_gap, dot, short_gap, dot, // s
            short_gap, short_gap,  // o
            dot, short_gap, dot, short_gap, dot, // s
    };
    ImageView imghelp;
    TextView tvHNama;
    EditText etSolusi, etMasalah;
    String sLat, sLon, sTokens, sIdHelp = "0", b64 = "", sIMEI;
    SessionManager session;
    JSONObject jResponse = new JSONObject(), jData = new JSONObject();
    int imgklik = 0, dores = 0, jemaahke = 0, buzz = 0;
    ImageView imgsend;
    Uri mCropImageUri;
    Bitmap myBitmap;
    View vHelp, vHelpRespon, vLoading, vHelpMap;
    ViewGroup backView;
    boolean visible = true;
    GoogleMap map;
    GoogleMapOptions options = new GoogleMapOptions();
    SupportMapFragment fragMap;
    //    String[] sTeleponJemaah, sLats, sLons, sImg, sName, sId;
    String sTeleponJemaah, sLats, sLons, sImg, sName, sId;
    TextView tvdmNama, tvdmTelp, tvTipe, tvLast;
    ImageView imgdm;
    Handler mHandler1;
    MarkerOptions markerme;
    Marker me, je;
    Button btnBuzz, btnSampe, btnSampeRes;
    private final Runnable m_Runnable = new Runnable() {
        public void run()

        {
            UpdateLocation();

            mHandler1.postDelayed(m_Runnable, 5000);
        }

    };
    List<RowItemHelp> rowItemHelp = new ArrayList<>();
    private Vibrator vib;
    private MediaPlayer mp;
    int isOne = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        buildGoogleApiClient();
        vHelp = findViewById(R.id.rlHelp);
        vHelpRespon = findViewById(R.id.rlHelpRespon);
        vHelpMap = findViewById(R.id.HelpMap);
        vLoading = findViewById(R.id.loadingView);
        backView = (ViewGroup) findViewById(R.id.activity_help);

        session = new SessionManager(getApplicationContext());

        this.mHandler1 = new Handler();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("snama") != null) {

                sName = extras.getString("snama");
                sImg = extras.getString("simg");
                sId = extras.getString("sid");
                sLats = extras.getString("lats");
                sLons = extras.getString("lons");
                sTeleponJemaah = extras.getString("sphone");
                Log.w(TAG, "onCreate: " + extras.getString("types"));
                rowItemHelp = new ArrayList<>();
                rowItemHelp.add(new RowItemHelp(sName, sImg, sId, sLats, sLons, sTeleponJemaah));
            } else if (extras.getString("help") != null) {
                sName = session.getA();
                sImg = session.getB();
                sLats = session.getC();
                sLons = session.getD();
                sTeleponJemaah = session.getE();
                sId = session.getF();
                dores = session.getG();
                sIdHelp = session.getH();
                Log.w(TAG, "onCreate: Dores " + String.valueOf(dores));
                TransitionSet seta = new TransitionSet()
                        .addTransition(new Scale(0.7f))
                        .addTransition(new Fade())
                        .setDuration(500)
                        .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                TransitionManager.beginDelayedTransition(backView, seta);
                vHelp.setVisibility(View.GONE);
                vHelpMap.setVisibility(View.VISIBLE);
                mHandler1.postDelayed(m_Runnable, 1000);
                dores = 1;
                Log.w(TAG, "onCreate: Get Saved " + sName + " " + sImg + " " + sLats + " " + sLons + " " + sTeleponJemaah + " " + sId + " " + dores + " " + sIdHelp);

            } else if (extras.getString("helpr") != null) {
                Log.w(TAG, "onCreate: Get Saved Res");
                sName = session.getA();
                sImg = session.getB();
                sLats = session.getC();
                sLons = session.getD();
                sTeleponJemaah = session.getE();
                sId = session.getF();
                dores = session.getG();
                sIdHelp = session.getH();
                Log.w(TAG, "onCreate: Dores " + String.valueOf(dores));
                TransitionSet seta = new TransitionSet()
                        .addTransition(new Scale(0.7f))
                        .addTransition(new Fade())
                        .setDuration(500)
                        .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                TransitionManager.beginDelayedTransition(backView, seta);
                vHelpRespon.setVisibility(View.VISIBLE);
                vHelpMap.setVisibility(View.GONE);
                vHelp.setVisibility(View.GONE);
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        fragMap = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fragMap.getMapAsync(this);

        tvdmNama = (TextView) findViewById(R.id.tvdmNama);
        tvdmTelp = (TextView) findViewById(R.id.tvdmTelp);

        tvTipe = (TextView) findViewById(R.id.tvtipemarker);
        tvLast = (TextView) findViewById(R.id.tvlastupdate);

        imgdm = (ImageView) findViewById(R.id.iconsmarker);
        imgdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(HelpActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.fullimg);
                dialog.setCancelable(true);
                dialog.show();
                ImageView zoom = (ImageView) dialog.findViewById(R.id.imgzoom);
                zoom.setImageDrawable(imgdm.getDrawable());
                zoom.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();

                    }
                });
            }
        });

        if (dores == 0) {
        /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
            final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
            this.mWakeLock.acquire();

            mp = new MediaPlayer();
            mp = MediaPlayer.create(this, R.raw.alarm);
            vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(pattern, 0);
            mp.start();
            mp.setLooping(true);
        }


        imghelp = (ImageView) findViewById(R.id.iconshelp);
        tvHNama = (TextView) findViewById(R.id.tvhNama);

        Picasso.with(this)
                .load(sImg)
                .placeholder(R.drawable.profilee)
                .into(imghelp);

        tvHNama.setText(sName);

        // Session class instance
        sTokens = session.getToken();
        sIMEI = session.getImei();

        sLat = session.getLat();
        sLon = session.getLon();
        Log.w(TAG, "onCreate: " + sLat + "\n" + sLon);
        etMasalah = (EditText) findViewById(R.id.tvMasalah);
        etSolusi = (EditText) findViewById(R.id.tvSolusi);
        imgsend = (ImageView) findViewById(R.id.imgsendhelp);

        btnBuzz = (Button) findViewById(R.id.buttonBuzz);
        btnSampe = (Button) findViewById(R.id.buttonSampe);
        btnSampeRes = (Button) findViewById(R.id.buttonSampeRes);

        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.w(TAG, "onReceive: " + action);
                // checking for type intent filter
                if (action.equals(Config.PUSH_NOTIFICATION)) {
                    Log.e(TAG, "onReceive: PUSH");
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        String type = extras.getString("typess");
                        assert type != null;
                        Log.w(TAG, "onReceive: " + type);
                        Log.w(TAG, "onReceive: Cancel");
                        String sid = extras.getString("si");
                        Log.w(TAG, "onReceive: " + sid);

                        for (int i = 0; i < rowItemHelp.size(); i++) {
                            if (rowItemHelp.get(i).getsId().equals(sid)) {
                                rowItemHelp.remove(i);
                                break;
                            }
                        }

                        if (rowItemHelp.size() > 0) {
                            // true
                            Log.w(TAG, "onReceive: Delete");

                            Picasso.with(HelpActivity.this)
                                    .load(rowItemHelp.get(0).getsImg())
                                    .placeholder(R.drawable.profilee)
                                    .into(imghelp);

                            tvHNama.setText(rowItemHelp.get(0).getsNama());
                        } else {
                            if (dores == 0) {
                                if (mp != null) {
                                    try {
                                        if (mp.isPlaying()) {
                                            mp.stop();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                vib.cancel();
                                finish();
                            }
                        }

                    }
                }// checking for type intent filter
                else if (action.equals(Config.PUSH_NOTIFICATIONADA)) {
                    Log.e(TAG, "onReceive: PUSH BARU");
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        String type = extras.getString("types");
                        assert type != null;
                        Log.w(TAG, "onReceive: " + type);
                        if (type.equals("tambah")) {
                            if (extras.getString("snama") != null) {

                                sName = extras.getString("snama");
                                sImg = extras.getString("simg");
                                sId = extras.getString("sid");
                                sLats = extras.getString("lats");
                                sLons = extras.getString("lons");
                                sTeleponJemaah = extras.getString("sphone");
                                Log.w(TAG, "onReceive: " + sName);
                                rowItemHelp.add(new RowItemHelp(sName, sImg, sId, sLats, sLons, sTeleponJemaah));
                            }
                        }
                    }
                }
            }
        };

        if (sName != null) {
            tvdmNama.setText(sName);
            tvdmTelp.setText(sTeleponJemaah);
            Picasso.with(HelpActivity.this)
                    .load(sImg)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .placeholder(R.drawable.profilee)
                    .into(imgdm);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
//        if (dores == 1) {
//            savedInstanceState.putString("MyString1", sName);
//            savedInstanceState.putString("MyString2", sImg);
//            savedInstanceState.putString("MyString3", sLats);
//            savedInstanceState.putString("MyString4", sLons);
//            savedInstanceState.putString("MyString5", sTeleponJemaah);
//            savedInstanceState.putString("MyString6", sId);
//            savedInstanceState.putInt("MyString7", dores);
//            session.createHelp("help");
//            session.createDataHelp(sName, sImg, sLats, sLons, sTeleponJemaah, sId, dores, sIdHelp);
//            Log.w(TAG, "onSaveInstanceState: Saving 1");
//        } else if (dores == 2) {
//            savedInstanceState.putString("MyString1", sName);
//            savedInstanceState.putString("MyString2", sImg);
//            savedInstanceState.putString("MyString3", sLats);
//            savedInstanceState.putString("MyString4", sLons);
//            savedInstanceState.putString("MyString5", sTeleponJemaah);
//            savedInstanceState.putString("MyString6", sId);
//            savedInstanceState.putInt("MyString7", dores);
//            session.createHelp("helpr");
//            session.createDataHelp(sName, sImg, sLats, sLons, sTeleponJemaah, sId, dores, sIdHelp);
//            Log.w(TAG, "onSaveInstanceState: Saving 2");
//        }
        // etc.

//        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();  // Put the values from the UI
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
//        if (dores == 1) {
//            sName = savedInstanceState.getString("MyString1");
//            sImg = savedInstanceState.getString("MyString2");
//            sLats = savedInstanceState.getString("MyString3");
//            sLons = savedInstanceState.getString("MyString4");
//            sTeleponJemaah = savedInstanceState.getString("MyString5");
//            sId = savedInstanceState.getString("MyString6");
//            dores = savedInstanceState.getInt("MyString7");
//            Log.w(TAG, "onRestoreInstanceState: Restore");
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dores == 0) {

            this.mWakeLock.release();
        } else if (dores == 1) {
            Log.w(TAG, "onDestroy: ");
            session.createHelp("help");
            session.createDataHelp(sName, sImg, sLat, sLon, sTeleponJemaah, sId, dores, sIdHelp);
        } else if (dores == 2) {

            session.createHelp("helpr");
            session.createDataHelp(sName, sImg, sLats, sLons, sTeleponJemaah, sId, dores, sIdHelp);
        }

        session.createDataHelp(sName, sImg, sLats, sLons, sTeleponJemaah, sId, dores, sIdHelp);
    }

    @Override
    public void onBackPressed() {

        session.createDataHelp(sName, sImg, sLats, sLons, sTeleponJemaah, sId, dores, sIdHelp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
//        if (sa == 0) {
        session.createHelp("help");
        session.createDataHelp(sName, sImg, sLats, sLons, sTeleponJemaah, sId, dores, sIdHelp);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATIONADA));
    }

    public void DoRespon(View v) {

        if (isOne == 0) {
            createLocationRequest();
            buildLocationSettingsRequest();
            checkLocationSettings();
        } else {
            Log.w(TAG, "DoRespon: " + Constant.HelpResponse + sTokens + "?latitude=" + sLat + "&longitude=" + sLon + "&part_id=" + sId);
            LoopjHttpClient.get(Constant.HelpResponse + sTokens + "?latitude=" + sLat + "&longitude=" + sLon + "&part_id=" + rowItemHelp.get(0).getsId() + "&imei=" + sIMEI, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                    TransitionSet seta = new TransitionSet()
                            .addTransition(new Scale(0.7f))
                            .addTransition(new Fade())
                            .setDuration(500)
                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                    TransitionManager.beginDelayedTransition(backView, seta);
                    vHelp.setVisibility(View.GONE);
                    vLoading.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.e(TAG, "onSuccess: " + String.valueOf(response));
                    try {
                        jResponse = response.getJSONObject("response");
                        if (jResponse.has("help_id"))
                            sIdHelp = jResponse.getString("help_id");

                        if (jResponse.getBoolean("status")) {
                            tvdmNama.setText(rowItemHelp.get(0).getsNama());
                            tvdmTelp.setText(rowItemHelp.get(0).getsPhone());

                            Picasso.with(HelpActivity.this)
                                    .load(rowItemHelp.get(0).getsImg())
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .placeholder(R.drawable.profilee)
                                    .into(imgdm);

                            mp.stop();
                            mp.release();
                            vib.cancel();
                            TransitionSet seta = new TransitionSet()
                                    .addTransition(new Scale(0.7f))
                                    .addTransition(new Fade())
                                    .setDuration(500)
                                    .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                            TransitionManager.beginDelayedTransition(backView, seta);
                            vLoading.setVisibility(View.GONE);
                            vHelpMap.setVisibility(View.VISIBLE);
                            dores = 1;
                            mHandler1.postDelayed(m_Runnable, 1000);
                            session.createDataHelp(rowItemHelp.get(0).getsNama(), rowItemHelp.get(0).getsImg(), sLats, sLons, rowItemHelp.get(0).getsPhone(), sId, dores, sIdHelp);
                        } else {
                            if (mp != null) {
                                try {
                                    if (mp.isPlaying()) {
                                        mp.stop();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            vib.cancel();
                            Toast.makeText(HelpActivity.this, "Jemaah sudah dibantu petugas lain.", Toast.LENGTH_LONG).show();
                            Intent in = new Intent(HelpActivity.this, MapsActivity.class);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            in.putExtra("done", "done");
                            startActivity(in);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "onFailure: " + String.valueOf(responseString));
                }
            });
        }
    }

    public void DoResponDone(View v) {

        if (etMasalah.getText().length() == 0) {
            etMasalah.setError("Harap diisi");
        } else if (etSolusi.getText().length() == 0) {
            etSolusi.setError("Harap diisi");
        } else if (imgklik == 1) {
            BitmapDrawable drawable = (BitmapDrawable) imgsend.getDrawable();
            myBitmap = drawable.getBitmap();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            byte[] ba = bytes.toByteArray();
            b64 = Base64.encodeToString(ba, Base64.NO_WRAP);

            Send();
        } else {
            Send();
        }
    }

    public void DoResponSampe(View v) {
        TransitionSet seta = new TransitionSet()
                .addTransition(new Scale(0.7f))
                .addTransition(new Fade())
                .setDuration(500)
                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
        TransitionManager.beginDelayedTransition(backView, seta);
        btnSampe.setVisibility(View.GONE);
        btnSampeRes.setVisibility(View.VISIBLE);
        DoBuzzerCancel();
    }

    public void DoResponSampeRes(View v) {
        TransitionSet seta = new TransitionSet()
                .addTransition(new Scale(0.7f))
                .addTransition(new Fade())
                .setDuration(500)
                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
        TransitionManager.beginDelayedTransition(backView, seta);
        vHelpMap.setVisibility(View.GONE);
        vHelpRespon.setVisibility(View.VISIBLE);
        dores = 2;
        DoBuzzerCancel();
//        DoArrive();
    }

    public void DoArrive() {
        LoopjHttpClient.get(Constant.Arrive + sTokens + "?help_id=" + sIdHelp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, "onSuccess: " + String.valueOf(response));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: " + String.valueOf(responseString));
            }
        });
    }

    public void DoBuzzer(View v) {
        if (buzz == 0) {
            LoopjHttpClient.get(Constant.Buzzer + sTokens + "?help_id=" + sIdHelp, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.e(TAG, "onSuccess: DoBuzzer " + String.valueOf(response));
                    buzz = 1;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "onFailure: DoBuzzer " + String.valueOf(responseString));
                }
            });
        } else {
            Toast.makeText(this, "Anda sudah menggunakan buzz.", Toast.LENGTH_SHORT).show();
        }
    }

    public void DoBuzzerCancel() {
        LoopjHttpClient.get(Constant.BuzzerCancel + sTokens + "?help_id=" + sIdHelp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, "onSuccess: DoBuzzer " + String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: DoBuzzer " + String.valueOf(responseString));
            }
        });
    }

    public void Send() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("help_id", sIdHelp);
        requestParams.put("help_problem", etMasalah.getText().toString());
        requestParams.put("help_image", b64);
        requestParams.put("solution", etSolusi.getText().toString());
        requestParams.put("imei", sIMEI);
        StringEntity entity = null;

        try {
            entity = new StringEntity(requestParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(this, Constant.HelpSolution + sTokens, entity, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                TransitionSet seta = new TransitionSet()
                        .addTransition(new Scale(0.7f))
                        .addTransition(new Fade())
                        .setDuration(500)
                        .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                TransitionManager.beginDelayedTransition(backView, seta);
                vHelpRespon.setVisibility(View.GONE);
                vLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, "onSuccess: " + String.valueOf(response));
                dores = 3;
                session.createHelp("kosong");
                session.CreateisHelp(false);

                if (mHandler1 != null)
                    mHandler1.removeCallbacks(m_Runnable);

                Toast.makeText(HelpActivity.this, "Terkirim", Toast.LENGTH_LONG).show();
                Intent in = new Intent(HelpActivity.this, MapsActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                in.putExtra("done", "done");
                startActivity(in);
                finish();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: " + String.valueOf(responseString));
                TransitionSet seta = new TransitionSet()
                        .addTransition(new Scale(0.7f))
                        .addTransition(new Fade())
                        .setDuration(500)
                        .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                TransitionManager.beginDelayedTransition(backView, seta);
                vHelpRespon.setVisibility(View.VISIBLE);
                vLoading.setVisibility(View.GONE);
                Toast.makeText(HelpActivity.this, "Koneksi bermasalah silahkan coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void HelpImage(View v) {
        onSelectImageClick(v);
    }

    /**
     * Start pick image activity with chooser.
     */
    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgsend.setImageURI(result.getUri());
                imgklik = 1;
//                Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Disini
                        isOne = 1;
                        DoRespon(vHelp);
//                        CheckDataLogin();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(false)
                .start(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        fragMap.getView().getViewTreeObserver().addOnGlobalLayoutListener(this);
        setUpMap();
        Log.e(TAG, "onMapReady: " + sLat);
        if (sLat.equals("nil")) {
            Toast.makeText(this, "Lokasi belum tersedia", Toast.LENGTH_SHORT).show();
        } else {


//            double Latitude = Double.parseDouble(sLats);
//            double Longitude = Double.parseDouble(sLons);
//            LatLng latlng = new LatLng(Latitude, Longitude);
////             add marker to the map
//            Marker markerJemaahA = map.addMarker(new MarkerOptions()
//                    .title("Jemaah")
//                    .position(latlng)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.hijau)
//                    ));
//            map.animateCamera(CameraUpdateFactory.newLatLngZoom(markerJemaahA.getPosition(), 17f));
        }
    }

    // method to set up map
    void setUpMap() {
        map.getUiSettings().setMapToolbarEnabled(false);
        options.mapType(GoogleMap.MAP_TYPE_NORMAL);
        options.compassEnabled(false);
        options.rotateGesturesEnabled(false);
        options.tiltGesturesEnabled(false);
        options.zoomControlsEnabled(false);
        SupportMapFragment.newInstance(options);
    }

    public void Callnum(View v) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvdmTelp.getText().toString()));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);

//        Intent dial = new Intent();
//        dial.setAction("android.intent.action.DIAL");
//        dial.setData(Uri.parse("tel:"+tvdmTelp.getText().toString()));
//        startActivity(dial);
    }

    public void Message(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            // Add the phone number in the data
            Uri uri = Uri.parse("smsto:" + tvdmTelp.getText().toString());
            // Create intent with the action and data
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
            // smsIntent.setData(uri); // We just set the data in the constructor above
            // Set the message
            smsIntent.putExtra("sms_body", "");

            startActivity(smsIntent);

        } else // For early versions, do what worked for you before.
        {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", tvdmTelp.getText().toString());
            smsIntent.putExtra("sms_body", "");
            startActivity(smsIntent);
        }
    }

    public void Navigator(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + sLat + "," + sLon));
//        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    @Override
    public void onGlobalLayout() {
        if (map != null) {
            if (dores == 1) {
                double Latitude = Double.parseDouble(sLats);
                double Longitude = Double.parseDouble(sLons);
                double MyLatitude = Double.parseDouble(sLat);
                double MyLongitude = Double.parseDouble(sLon);

                LatLng petugasLocation = new LatLng(Latitude, Longitude);

                LatLng jemaahLocation = new LatLng(MyLatitude, MyLongitude);
                map.clear();
                je = map.addMarker(new MarkerOptions().position(petugasLocation).title("Jemaah").icon(BitmapDescriptorFactory.fromResource(R.drawable.markhijau)));
                me = map.addMarker(new MarkerOptions().position(jemaahLocation).title("Lokasi Anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.markmerah)));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            for (Marker marker : markers) {
                builder.include(me.getPosition());
                builder.include(je.getPosition());
//            }
                LatLngBounds bounds = builder.build();


                int padding = 80; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                map.animateCamera(cu);
            }
        }
    }

    void UpdateLocation() {
        LoopjHttpClient.get(Constant.UpdateLocation + sTokens + "?person_id=" + session.getIds(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, "onSuccess: " + String.valueOf(response));
                Boolean status;
                try {
                    jResponse = response.getJSONObject("response");
                    jData = jResponse.getJSONObject("data");
                    status = jResponse.getBoolean("status");
                    if (status) {
                        String latbaru = jData.getString("latitude");
                        String lonbaru = jData.getString("longitude");

                        double Latitude = Double.parseDouble(latbaru);
                        double Longitude = Double.parseDouble(lonbaru);
                        LatLng lol = new LatLng(Latitude, Longitude);

                        int a = CalculationByDistance(lol, je.getPosition());
                        if (a <= 60) {
                            btnBuzz.setEnabled(true);
                        }
//                        animateMarker(me, lol, false);
                        LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
                        animateMarkerToGB(me, lol, latLngInterpolator);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: " + String.valueOf(responseString));
            }
        });
    }

    void animateMarkerToGB(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(marker.getPosition());
                builder.include(je.getPosition());
                LatLngBounds bounds = builder.build();

                int padding = 80; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                map.animateCamera(cu);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    /**
     * calculates the distance between two locations in MILES
     */
    public int CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

//        return Radius * c;
        return meterInDec;
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (ConnectivityReceiver.isConnected()) {
//            if (checkPlayServices()) {
                if (mGoogleApiClient != null)
                    mGoogleApiClient.connect();
//            }
        }
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
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
//        if (checkPlayServices()) {
            mGoogleApiClient.connect();
//        }
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
                isOne = 1;
                DoRespon(vHelp);
                // disini
//                CheckDataLogin();
//                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.e(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(HelpActivity.this, REQUEST_CHECK_SETTINGS);
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            // Check for the integer request code originally supplied to startResolutionForResult().
//            case REQUEST_CHECK_SETTINGS:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        Log.i(TAG, "User agreed to make required location settings changes.");
//                        // Disini
////                        CheckDataLogin();
////                        startLocationUpdates();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Log.i(TAG, "User chose not to make required location settings changes.");
//                        checkLocationSettings();
//                        break;
//                }
//                break;
//        }
//    }
}
