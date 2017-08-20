package com.huji.apps.haji.petugas;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.huji.apps.haji.petugas.Utils.Config;
import com.huji.apps.haji.petugas.Utils.Constant;
import com.huji.apps.haji.petugas.Utils.LatLngInterpolator;
import com.huji.apps.haji.petugas.Utils.LoopjHttpClient;
import com.huji.apps.haji.petugas.Utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HelpPetugasActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "HelpPetugasActivity";
    public BroadcastReceiver mRegistrationBroadcastReceiver;
    int dot = 200; // Length of a Morse Code "dot" in milliseconds
    int short_gap = 0; // Length of Gap Between dots/dashes
    long[] pattern = {0, // Start immediately
            dot, short_gap, dot, short_gap, dot, // s
            short_gap, short_gap,  // o
            dot, short_gap, dot, short_gap, dot, // s
    };
    String sLat, sLon, sImg, sName, sId, sTokens, sIdHelp, b64 = "";
    SessionManager session;
    int sa = 0;
    ImageView imgsend;
    Uri mCropImageUri;
    Bitmap myBitmap;
    View vHelp, vHelpRespon, vLoading, vHelpMap;
    ViewGroup backView;
    boolean visible = true;
    GoogleMap map;
    GoogleMapOptions options = new GoogleMapOptions();
    SupportMapFragment fragMap;
    String sNamaJemaah, sTeleponJemaah, sDatetime, sPhotoJemaah, sLats, sLons, sIdp,sIMEI;
    TextView tvdmNama, tvdmTelp, tvTipe, tvLast, tvHelpBuzz;
    ImageView imgdm;
    Handler mHandler1;
    Marker me, je;
    SpinKitView spinKitView;
    JSONObject jResponse = new JSONObject(), jData = new JSONObject();
    LatLngBounds bounds;
    CameraUpdate cu;
    private final Runnable m_Runnable = new Runnable() {
        public void run()

        {
            UpdateLocation();

            mHandler1.postDelayed(m_Runnable, 5000);
        }

    };
    Context ctx;
    private Vibrator vib;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_petugas);
        session = new SessionManager(this);

        sIMEI = session.getImei();

        Bundle extrasa = getIntent().getExtras();
        if (extrasa != null) {
            if (extrasa.getString("bcpimg") != null) {
                Log.w(TAG, "onCreate: Data Extra" + extrasa.getString("bcpimg"));
            }
        } else {
            Log.e(TAG, "onCreate: Kosong Extra ");
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.e(TAG, "onCreate: HELPS " + extras.getString("finish"));
            if (extras.getString("name") != null) {
                sName = extras.getString("name");
                sImg = extras.getString("image");
                sLat = extras.getString("latitude");
                sLon = extras.getString("longitude");
                sTeleponJemaah = extras.getString("sphone");
                sIdp = extras.getString("id");

            } else if (extras.getString("finish") != null) {
                Log.w(TAG, "onCreate: Get Finish");
                sName = session.getA();
                sImg = session.getB();
                sLat = session.getC();
                sLon = session.getD();
                sTeleponJemaah = session.getE();
                sIdp = session.getF();
                session.createHelp("kosong");
                sa = 1;
//                Toast.makeText(HelpPetugasActivity.this, "Petugas "+sName + " sudah sampai ditempat Anda", Toast.LENGTH_LONG).show();
                startActivity(new Intent(HelpPetugasActivity.this, MapsActivity.class));
                finish();
                return;

            } else if (extras.getString("help") != null) {
                Log.w(TAG, "onCreate: Get Saved");
                sName = session.getA();
                sImg = session.getB();
                sLat = session.getC();
                sLon = session.getD();
                sTeleponJemaah = session.getE();
                sIdp = session.getF();
            } else if (extras.getString("do") != null) {
//                        finish();
                String dos = extras.getString("do");
                if (dos.equalsIgnoreCase("buzz")) {
                    mp = new MediaPlayer();
                    mp = MediaPlayer.create(HelpPetugasActivity.this, R.raw.beep);
                    vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(pattern, 0);
                    mp.start();
                    mp.setLooping(true);
                    spinKitView.setVisibility(View.VISIBLE);

                } else if (dos.equalsIgnoreCase("finish")) {
                    if (mp != null) {
                        try {
                            if (mp.isPlaying()) {
                                mp.stop();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (vib != null) {
                        vib.cancel();
                    }

//                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HelpPetugasActivity.this);
//                            builder.setMessage("Petugas "+sName + " sudah sampai ditempat Anda");
//                            // Ini yang kemaren error.. karena depannya tidak dikasih builder.
//                            // sebelumnya :
//                            // setCancelable(false);
//                            builder.setCancelable(false);
//                            builder.setNegativeButton("OK", new
//                                    DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int arg1) {
//                                            // TODO Auto-generated method stub

                    session.createHelp("kosong");
//                            Toast.makeText(HelpPetugasActivity.this, "Petugas "+sName + " sudah sampai ditempat Anda", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(HelpPetugasActivity.this, MapsActivity.class));
                    finish();
//                                        }
//                                    }).show();

                } else if (dos.equalsIgnoreCase("cancel")) {
//                        finish();
                    if (mp != null) {
                        try {
                            if (mp.isPlaying()) {
                                mp.stop();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (vib != null) {
                        vib.cancel();
                    }
                }
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
        tvHelpBuzz = (TextView) findViewById(R.id.tvHelpBuzz);

        imgdm = (ImageView) findViewById(R.id.iconsmarker);
        imgdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(HelpPetugasActivity.this);
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

        spinKitView = (SpinKitView) findViewById(R.id.spin_kit);

        tvdmNama.setText(sName);
        tvdmTelp.setText(sTeleponJemaah);

        Picasso.with(this)
                .load(sImg)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.profilee)
                .into(imgdm);

        sTokens = session.getToken();
        Log.w(TAG, "onCreate: " + sTokens);
        this.mHandler1 = new Handler();
        mHandler1.postDelayed(m_Runnable, 1000);

        ctx = getApplicationContext();

        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_BUZZ)) {
                    String dos = intent.getStringExtra("do");
                    if (dos.equalsIgnoreCase("buzz")) {
                        mp = new MediaPlayer();
                        mp = MediaPlayer.create(HelpPetugasActivity.this, R.raw.beep);
                        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vib.vibrate(pattern, 0);
                        mp.start();
                        mp.setLooping(true);
                        spinKitView.setVisibility(View.VISIBLE);

                    } else if (dos.equalsIgnoreCase("finish")) {
                        if (mp != null) {
                            try {
                                if (mp.isPlaying()) {
                                    mp.stop();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (vib != null) {
                            vib.cancel();
                        }

//                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HelpPetugasActivity.this);
//                            builder.setMessage("Petugas "+sName + " sudah sampai ditempat Anda");
//                            // Ini yang kemaren error.. karena depannya tidak dikasih builder.
//                            // sebelumnya :
//                            // setCancelable(false);
//                            builder.setCancelable(false);
//                            builder.setNegativeButton("OK", new
//                                    DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int arg1) {
//                                            // TODO Auto-generated method stub

                        session.createHelp("kosong");
//                            Toast.makeText(HelpPetugasActivity.this, "Petugas "+sName + " sudah sampai ditempat Anda", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(HelpPetugasActivity.this, MapsActivity.class));
                        finish();
//                                        }
//                                    }).show();

                    } else if (dos.equalsIgnoreCase("cancel")) {
//                        finish();
                        if (mp != null) {
                            try {
                                if (mp.isPlaying()) {
                                    mp.stop();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (vib != null) {
                            vib.cancel();
                        }
                    }
                }
            }
        };

        if (sa == 0) {
            session.createHelp("help");
            session.createDataHelp(sName, sImg, sLat, sLon, sTeleponJemaah, sIdp);
        }
    }
//
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        // Save UI state changes to the savedInstanceState.
//        // This bundle will be passed to onCreate if the process is
//        // killed and restarted.
//        if (sa == 0) {
//            savedInstanceState.putString("MyString1", sName);
//            savedInstanceState.putString("MyString2", sImg);
//            savedInstanceState.putString("MyString3", sLat);
//            savedInstanceState.putString("MyString4", sLon);
//            savedInstanceState.putString("MyString5", sTeleponJemaah);
//            savedInstanceState.putString("MyString6", sIdp);
//            session.CreateisHelp();("help");
//            session.createDataHelp(sName, sImg, sLat, sLon, sTeleponJemaah, sIdp);
//            Log.w(TAG, "onSaveInstanceState: Saving");
//        }
//        // etc.
//
////        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
////        SharedPreferences.Editor editor = preferences.edit();  // Put the values from the UI
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
//        sName = savedInstanceState.getString("MyString1");
//        sImg = savedInstanceState.getString("MyString2");
//        sLat = savedInstanceState.getString("MyString3");
//        sLon = savedInstanceState.getString("MyString4");
//        sTeleponJemaah = savedInstanceState.getString("MyString5");
//        sIdp = savedInstanceState.getString("MyString6");
//        Log.w(TAG, "onRestoreInstanceState: Restore");
//    }

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
    protected void onDestroy() {
        if (sa == 0) {
            session.createHelp("help");
            session.createDataHelp(sName, sImg, sLat, sLon, sTeleponJemaah, sIdp);
        }
        super.onDestroy();
        Log.w(TAG, "onDestroy: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        if (sa == 0) {
            session.createHelp("help");
            session.createDataHelp(sName, sImg, sLat, sLon, sTeleponJemaah, sIdp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_BUZZ));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.poi));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        map = googleMap;
        setUpMap();
        Log.e(TAG, "onMapReady: " + sLat);
        if (sLat.equals("nil")) {
            Toast.makeText(this, "Lokasi belum tersedia", Toast.LENGTH_SHORT).show();
        } else {
            double Latitude = Double.parseDouble(sLat);
            double Longitude = Double.parseDouble(sLon);
            double MyLatitude = Double.parseDouble(session.getLat());
            double MyLongitude = Double.parseDouble(session.getLon());
            LatLng petugasLocation = new LatLng(Latitude, Longitude);

            LatLng jemaahLocation = new LatLng(MyLatitude, MyLongitude);

            ArrayList<Marker> markers = new ArrayList<>();

            me = map.addMarker(new MarkerOptions().position(petugasLocation).title("Petugas").icon(BitmapDescriptorFactory.fromResource(R.drawable.markmerah)));
            je = map.addMarker(new MarkerOptions().position(jemaahLocation).title("Lokasi Anda").icon(BitmapDescriptorFactory.fromResource(R.drawable.markhijau)));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            for (Marker marker : markers) {
            builder.include(me.getPosition());
            builder.include(je.getPosition());
//            }
            bounds = builder.build();

            int padding = 80; // offset from edges of the map in pixels
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition arg0) {
                    map.animateCamera(cu);
                }
            });
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

    @Override
    public void onBackPressed() {
        if (sa == 0) {
            session.createHelp("help");
            session.createDataHelp(sName, sImg, sLat, sLon, sTeleponJemaah, sIdp);
        }
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
                bounds = builder.build();

                int padding = 80; // offset from edges of the map in pixels
                cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                map.animateCamera(cu);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }


    void UpdateLocation() {
        LoopjHttpClient.get(Constant.UpdateLocation + sTokens + "?person_id=" + sIdp, new JsonHttpResponseHandler() {
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


}
