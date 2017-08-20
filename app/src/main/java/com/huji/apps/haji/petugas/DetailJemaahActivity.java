package com.huji.apps.haji.petugas;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.huji.apps.haji.petugas.Utils.Constant;
import com.huji.apps.haji.petugas.Utils.LoopjHttpClient;
import com.huji.apps.haji.petugas.Utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static com.huji.apps.haji.petugas.Utils.Constant.RESPONSE;

public class DetailJemaahActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "DetailJemaahActivity";
    // TODO

    GoogleMap map;
    GoogleMapOptions options = new GoogleMapOptions();
    SupportMapFragment fragMap;
    String sNamaJemaah, sTeleponJemaah, sDatetime, sPhotoJemaah, sLat, sLon, sTokens, sId, sDate;
    TextView tvdmNama, tvdmTelp, tvTipe, tvLast;

    ImageView imgdm;
    SessionManager sessionManager;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfdate = new SimpleDateFormat("dd MMMM yyyy");
    SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat formawal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String[] slatj, slonj, sdates;

    JSONObject jResponse = new JSONObject(), jData = new JSONObject(), jDatas = new JSONObject();

    JSONArray jAJemaah = new JSONArray(), jAPetugas = new JSONArray();
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jemaah);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Detail Jemaah");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        fragMap = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        tvdmNama = (TextView) findViewById(R.id.tvdmNama);
        tvdmTelp = (TextView) findViewById(R.id.tvdmTelp);

        tvTipe = (TextView) findViewById(R.id.tvtipemarker);
        tvLast = (TextView) findViewById(R.id.tvlastupdate);

        imgdm = (ImageView) findViewById(R.id.iconsmarker);
        imgdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(DetailJemaahActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));
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

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if (extras.getString("nama") != null) {
                sNamaJemaah = extras.getString("nama");
                sTeleponJemaah = extras.getString("telepon");
                sDatetime = extras.getString("timeup");
                sPhotoJemaah = extras.getString("photo");
                sLat = extras.getString("lat");
                sLon = extras.getString("lon");
            }
        }

        tvdmNama.setText(sNamaJemaah);
        tvdmTelp.setText(sTeleponJemaah);
        tvTipe.setText("Jemaah");
        if (!sDatetime.equals("nil")) {
            tvLast.setText("Update terakhir : " + sDatetime);
        }
        Picasso.with(this)
                .load(sPhotoJemaah)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.profilee)
                .into(imgdm);

        fragMap.getMapAsync(this);

        sessionManager = new SessionManager(this);
        sTokens = sessionManager.getToken();
        sId = sessionManager.getIds();
        sDate = sdf.format(new Date()) + " 00:00:00";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setUpMap();
        Log.e(TAG, "onMapReady: " + sLat );
        if (sLat.equals("nil")) {
            Toast.makeText(this, "Lokasi belum tersedia", Toast.LENGTH_SHORT).show();
        } else {
            double Latitude = Double.parseDouble(sLat);
            double Longitude = Double.parseDouble(sLon);
            LatLng latlng = new LatLng(Latitude, Longitude);
//             add marker to the map
            Marker markerJemaahA = map.addMarker(new MarkerOptions()
                    .title("Lokasi Terakhir")
                    .position(latlng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markhijau)
                    ));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(markerJemaahA.getPosition(), 17f));
            GetPeroidic();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
    }

    public void GetPeroidic () {
        LoopjHttpClient.get(Constant.GetPeriodic + sTokens+ "?person_id=" + sId + "&start_time=" + sDate +"&stop_time="+sDatetime , new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, "onSuccess: GetPeriodic " + String.valueOf(response) );
                try {
                    jResponse = response.getJSONObject(RESPONSE);
                    if (jResponse.has("data")) {
                        jDatas = jResponse.getJSONObject("data");
                        jAJemaah = jDatas.getJSONArray("periodic_location");
                        slatj = new String[jAJemaah.length()];
                        slonj = new String[jAJemaah.length()];
                        sdates = new String[jAJemaah.length()];
                        for (int i = 0; i < jAJemaah.length(); i++) {
                            jData = jAJemaah.getJSONObject(i);
                            slatj[i] = jData.getString("latitude");
                            slonj[i] = jData.getString("longitude");
                            sdates[i] = jData.getString("datetime");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                if (slatj.length > 0) {
//                    String jam ="", tgl="";
//                    PolylineOptions options = new PolylineOptions().width(5).color(Color.parseColor("#2196F3")).geodesic(true);
//                    for (int z = 0; z < slatj.length; z++) {
//                        try {
//                            Date newDate = formawal.parse(sdates[z]);
//                            jam = sdftime.format(newDate);
//                            tgl = sdfdate.format(newDate);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                        LatLng point = new LatLng(Double.parseDouble(slatj[z]), Double.parseDouble(slonj[z]));
//                        options.add(point);
//                        options.geodesic(true);
//                        Marker markerJemaahA = map.addMarker(new MarkerOptions()
//                                .title(jam)
//                                .snippet(tgl)
//                                .position(point)
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedots)
//                                ));
//                    }
//
//                    map.addPolyline(options);
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: GetPeriodic " + String.valueOf(responseString) );
            }
        });
    }
}
