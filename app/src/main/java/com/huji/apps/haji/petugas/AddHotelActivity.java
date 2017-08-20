package com.huji.apps.haji.petugas;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.huji.apps.haji.petugas.Adapters.CustomListViewHotelAdapter;
import com.huji.apps.haji.petugas.Beans.RowItemHotel;
import com.huji.apps.haji.petugas.Utils.Constant;
import com.huji.apps.haji.petugas.Utils.LoopjHttpClient;
import com.huji.apps.haji.petugas.Utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class AddHotelActivity extends AppCompatActivity implements OnMapReadyCallback {

    List<RowItemHotel> rowItemLjs = new ArrayList<>(), rowItemLjsMy = new ArrayList<>();
    ListView lvHotel, lvHotelMy;
    private TextView tvPlaceAPI, tvNo;
    // konstanta untuk mendeteksi hasil balikan dari place picker
    private int PLACE_PICKER_REQUEST = 1;
    String sToken, sLat, sLon, sPlaceName, sPlaceAddress, sType;
    TextView tvdmNama, tvdmTelp, tvHeaderMy,tvHeader;
    SessionManager sessionManager;
    private static final String TAG = "AddHotelActivity";

    String[] sId, slatj, slonj, splace, splaceaddress;

    JSONObject jResponse = new JSONObject(), jData = new JSONObject(), jDatas = new JSONObject();

    JSONArray jAJemaah = new JSONArray();
    Boolean status;
    ImageView imgdm;

    boolean visible = true;
    ViewGroup backview;
    View ldetHotel;
    int back = 0, pos;
    // TODO

    GoogleMap map;
    GoogleMapOptions options = new GoogleMapOptions();
    SupportMapFragment fragMap;
    Button buttonEnterFalses,btnMy,btnAddHotel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hotel);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        fragMap = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        setTitle("Hotel");

        sessionManager = new SessionManager(this);
        sToken = sessionManager.getToken();
        tvNo = (TextView) findViewById(R.id.tv_no);
        lvHotel = (ListView) findViewById(R.id.listhotel);
        tvHeader = (TextView) findViewById(R.id.tvHotel);
        lvHotelMy = (ListView) findViewById(R.id.listhotelMy);
        tvHeaderMy = (TextView) findViewById(R.id.tvHotelSaya);
//        buttonEnterFalses = (Button) findViewById(R.id.buttonEnterFalsesD);

        btnMy = (Button) findViewById(R.id.btnMyHotel);
        btnMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddHotel();
            }
        });

        btnAddHotel = (Button) findViewById(R.id.btnAddHotel);
        btnAddHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddHotel();
            }
        });

        tvdmNama = (TextView) findViewById(R.id.tvdmNama);
        tvdmTelp = (TextView) findViewById(R.id.tvdmTelp);
        imgdm = (ImageView) findViewById(R.id.navimg);

        backview = (ViewGroup) findViewById(R.id.layout_add_hotel);
        ldetHotel = findViewById(R.id.ldetHotel);
        sType = sessionManager.getType();
        InitHotel();
    }

    public void InitHotel() {
//        rowItemLjs = new ArrayList<>();
//        for (int i = 0; i < slatj.length; i++) {
//            rowItemLjs.add(new RowItemHotel(slatj[i], slonj[i], splace[i], splaceaddress[i]));
//        }
//        if (slatj.length == 0) {
//            tvNo.setVisibility(View.VISIBLE);
//        } else {
//            tvNo.setVisibility(View.GONE);
//            lvHotel.setVisibility(View.VISIBLE);
//            tvHeader.setVisibility(View.VISIBLE);
//            CustomListViewHotelAdapter adapter = new CustomListViewHotelAdapter(this, R.layout.itemhotel, rowItemLjs);
//            adapter.notifyDataSetChanged();
//            lvHotel.setAdapter(adapter);
//            lvHotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                    fragMap.getMapAsync(LokasiHotelActivity.this);
//                    TransitionSet seta = new TransitionSet()
//                            .addTransition(new Scale(0.7f))
//                            .addTransition(new Fade())
//                            .setDuration(500)
//                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
//                    TransitionManager.beginDelayedTransition(backview, seta);
//                    ldetHotel.setVisibility(View.VISIBLE);
//                    lvHotel.setVisibility(View.GONE);
//                    lvHotelMy.setVisibility(View.GONE);
//                    tvHeaderMy.setVisibility(View.GONE);
//                    tvHeader.setVisibility(View.GONE);
//                    btnMy.setVisibility(View.VISIBLE);
//                    tvdmNama.setText(splace[i]);
//                    tvdmTelp.setText(splaceaddress[i]);
//                    sLat = slatj[i];
//                    sLon = slonj[i];
//                    imgdm.setTag(i);
//                    back = 1;
//                    setTitle("Detail Hotel");
//                }
//            });
//        }
        TransitionSet seta = new TransitionSet()
                .addTransition(new Scale(0.7f))
                .addTransition(new Fade())
                .setDuration(500)
                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
        TransitionManager.beginDelayedTransition(backview, seta);
        ldetHotel.setVisibility(View.GONE);
        if (!sessionManager.getLatHotel().equalsIgnoreCase("0")) {

            rowItemLjsMy = new ArrayList<>();
            rowItemLjsMy.add(new RowItemHotel("asd",sessionManager.getLatHotel(), sessionManager.getLonHotel(), sessionManager.getNamaHotel(), sessionManager.getAlamatHotel()));
            tvNo.setVisibility(View.GONE);
            btnAddHotel.setVisibility(View.GONE);
            lvHotelMy.setVisibility(View.VISIBLE);
            tvHeaderMy.setVisibility(View.VISIBLE);

            CustomListViewHotelAdapter adapter = new CustomListViewHotelAdapter(this, R.layout.itemhotel, rowItemLjsMy);
            adapter.notifyDataSetChanged();
            lvHotelMy.setAdapter(adapter);
            lvHotelMy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    fragMap.getMapAsync(AddHotelActivity.this);
                    TransitionSet seta = new TransitionSet()
                            .addTransition(new Scale(0.7f))
                            .addTransition(new Fade())
                            .setDuration(500)
                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                    TransitionManager.beginDelayedTransition(backview, seta);
                    ldetHotel.setVisibility(View.VISIBLE);
                    lvHotel.setVisibility(View.GONE);
                    lvHotelMy.setVisibility(View.GONE);
                    tvHeaderMy.setVisibility(View.GONE);
                    tvHeader.setVisibility(View.GONE);
                    tvdmNama.setText(sessionManager.getNamaHotel());
                    tvdmTelp.setText(sessionManager.getAlamatHotel());
                    sLat = sessionManager.getLatHotel();
                    sLon = sessionManager.getLonHotel();
                    imgdm.setTag(i);
                    back = 1;
                    invalidateOptionsMenu();
                    setTitle("Hotel Saya");
                }
            });
            setTitle("Hotel");
            back = 0;
        } else {
            lvHotelMy.setVisibility(View.GONE);
            tvHeaderMy.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (back == 0){
            super.onBackPressed();
        } else  {
            InitHotel();
//            TransitionSet seta = new TransitionSet()
//                    .addTransition(new Scale(0.7f))
//                    .addTransition(new Fade())
//                    .setDuration(500)
//                    .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
//            TransitionManager.beginDelayedTransition(backview, seta);
//            ldetHotel.setVisibility(View.GONE);
//
////            lvHotel.setVisibility(View.VISIBLE);
////            tvHeader.setVisibility(View.VISIBLE);
//            if (!sessionManager.getLatHotel().equalsIgnoreCase("0")) {
//                rowItemLjsMy = new ArrayList<>();
//                rowItemLjsMy.add(new RowItemHotel("MyHotel",sessionManager.getLatHotel(), sessionManager.getLonHotel(), sessionManager.getNamaHotel(), sessionManager.getAlamatHotel() ));
//                tvNo.setVisibility(View.GONE);
//                lvHotelMy.setVisibility(View.VISIBLE);
//                tvHeaderMy.setVisibility(View.VISIBLE);
//
//                CustomListViewHotelAdapter adapter = new CustomListViewHotelAdapter(this, R.layout.itemhotel, rowItemLjsMy);
//                adapter.notifyDataSetChanged();
//                lvHotelMy.setAdapter(adapter);
//                lvHotelMy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                        fragMap.getMapAsync(AddHotelActivity.this);
//                        TransitionSet seta = new TransitionSet()
//                                .addTransition(new Scale(0.7f))
//                                .addTransition(new Fade())
//                                .setDuration(500)
//                                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
//                        TransitionManager.beginDelayedTransition(backview, seta);
//                        ldetHotel.setVisibility(View.VISIBLE);
//                        lvHotel.setVisibility(View.GONE);
//                        lvHotelMy.setVisibility(View.GONE);
//                        tvHeaderMy.setVisibility(View.GONE);
//                        tvHeader.setVisibility(View.GONE);
//                        tvdmNama.setText(sessionManager.getNamaHotel());
//                        tvdmTelp.setText(sessionManager.getAlamatHotel());
//                        sLat = sessionManager.getLatHotel();
//                        sLon = sessionManager.getLonHotel();
//                        imgdm.setTag(i);
////                        buttonEnterFalses.setVisibility(View.GONE);
//                        btnMy.setVisibility(View.GONE);
//                        back = 1;
//                        setTitle("Hotel Saya");
//                        invalidateOptionsMenu();
//                    }
//                });
//            } else {
//                lvHotelMy.setVisibility(View.GONE);
//                tvHeaderMy.setVisibility(View.GONE);
//            }
            invalidateOptionsMenu();
            setTitle("Hotel");
            back = 0;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        if (id == R.id.add_hotel) {
            // membuat Intent untuk Place Picker
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                //menjalankan place picker
                startActivityForResult(builder.build(AddHotelActivity.this), PLACE_PICKER_REQUEST);

                // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate( R.menu.hotel_menu, menu );
////        if (sType.equalsIgnoreCase("ketua kloter")) {
//            if (back == 0) {
//                menu.findItem(R.id.add_hotel).setVisible(true);
//            } else {
//                menu.findItem(R.id.add_hotel).setVisible(false);
//            }
////        } else {
////            menu.findItem(R.id.add_hotel).setVisible(false);
////        }
//
//        return true;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format(
                        "Place: %s \n" +
                                "Alamat: %s \n" +
                                "Latlng %s \n", place.getName(), place.getAddress(), place.getLatLng().latitude+" "+place.getLatLng().longitude);

                sLat = String.format("%s",place.getLatLng().latitude);
                sLon = String.format("%s",place.getLatLng().longitude);
                sPlaceName = String.format("%s",place.getName());
                sPlaceAddress = String.format("%s", place.getAddress());
//                Send();
                SetMyHotel();
            }
        }
    }

    public void Send() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("latitude", sLat);
        requestParams.put("longitude", sLon);
        requestParams.put("place_name", sPlaceName);
        requestParams.put("place_address", sPlaceAddress);
        StringEntity entity = null;

        try {
            entity = new StringEntity(requestParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(this, Constant.SaveHotel + sToken, entity, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d(TAG, "onSuccess: " + String.valueOf(response));

                try {
                    jResponse = response.getJSONObject(Constant.RESPONSE);
                    status = jResponse.getBoolean(Constant.STATUS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status) {
                    Toast.makeText(AddHotelActivity.this, "Tersimpan", Toast.LENGTH_SHORT).show();
                    ListHotel();
                } else {
                    Toast.makeText(AddHotelActivity.this, "Coba lagi.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "onFailure: " + responseString );
                Toast.makeText(AddHotelActivity.this, "Koneksi Anda bermasalah silahkan coba lagi.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void ListHotel() {
        LoopjHttpClient.get(Constant.ListHotel + sToken, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, "onSuccess: " + String.valueOf(response));
                try {
                    jResponse = response.getJSONObject("response");
//                    if (jResponse.getBoolean("status")) {
                    if (jResponse.has("list_hotel")) {
                        jAJemaah = jResponse.getJSONArray("list_hotel");
                        if (jAJemaah.length() > 0) {
                            sId = new String[jAJemaah.length()];
                            slatj = new String[jAJemaah.length()];
                            slonj = new String[jAJemaah.length()];
                            splace = new String[jAJemaah.length()];
                            splaceaddress = new String[jAJemaah.length()];
                            for (int i = 0; i < jAJemaah.length(); i++) {
                                jData = jAJemaah.getJSONObject(i);
                                sId[i] = jData.getString("hotel_id");
                                slatj[i] = jData.getString("latitude");
                                slonj[i] = jData.getString("longitude");
                                splace[i] = jData.getString("place_name");
                                splaceaddress[i] = jData.getString("place_address");

                            }

                        } else {
                            rowItemLjs.clear();
                            slatj = new String[jAJemaah.length()];
                        }
                        InitHotel();
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

//    public void InitHotel() {
//        rowItemLjs = new ArrayList<>();
//        for (int i = 0; i < slatj.length; i++) {
//            rowItemLjs.add(new RowItemHotel(sId[i], slatj[i], slonj[i], splace[i], splaceaddress[i]));
//        }
//        if (slatj.length == 0) {
//            tvNo.setVisibility(View.VISIBLE);
//            lvHotel.setVisibility(View.GONE);
//        } else {
//            tvNo.setVisibility(View.GONE);
//            lvHotel.setVisibility(View.VISIBLE);
//            tvHeader.setVisibility(View.VISIBLE);
//            CustomListViewHotelAdapter adapter = new CustomListViewHotelAdapter(this, R.layout.itemhotel, rowItemLjs);
//            adapter.notifyDataSetChanged();
//            lvHotel.setAdapter(adapter);
//            lvHotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                    fragMap.getMapAsync(AddHotelActivity.this);
//                    TransitionSet seta = new TransitionSet()
//                            .addTransition(new Scale(0.7f))
//                            .addTransition(new Fade())
//                            .setDuration(500)
//                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
//                    TransitionManager.beginDelayedTransition(backview, seta);
//                    ldetHotel.setVisibility(View.VISIBLE);
//
//                    lvHotel.setVisibility(View.GONE);
//                    lvHotelMy.setVisibility(View.GONE);
//                    tvHeaderMy.setVisibility(View.GONE);
//                    tvHeader.setVisibility(View.GONE);
//
//                    buttonEnterFalses.setVisibility(View.VISIBLE);
//                    tvdmNama.setText(splace[i]);
//                    tvdmTelp.setText(splaceaddress[i]);
//                    sLat = slatj[i];
//                    sLon = slonj[i];
//                    imgdm.setTag(i);
//                    back = 1;
//                    pos = i;
//                    btnMy.setVisibility(View.VISIBLE);
//                    setTitle("Detail Hotel");
//
////                    if (sType.equalsIgnoreCase("ketua kloter")) {
////                        buttonEnterFalses.setVisibility(View.VISIBLE);
////                    } else {
////                        buttonEnterFalses.setVisibility(View.GONE);
////                    }
//
//                    invalidateOptionsMenu();
//                }
//            });
//        }
//        if (!sessionManager.getLatHotel().equalsIgnoreCase("0")){
//
//            rowItemLjsMy = new ArrayList<>();
//            rowItemLjsMy.add(new RowItemHotel("MyHotels",sessionManager.getLatHotel(), sessionManager.getLonHotel(), sessionManager.getNamaHotel(), sessionManager.getAlamatHotel() ));
//            tvNo.setVisibility(View.GONE);
//            lvHotelMy.setVisibility(View.VISIBLE);
//            tvHeaderMy.setVisibility(View.VISIBLE);
//
//            CustomListViewHotelAdapter adapter = new CustomListViewHotelAdapter(this, R.layout.itemhotel, rowItemLjsMy);
//            adapter.notifyDataSetChanged();
//            lvHotelMy.setAdapter(adapter);
//            lvHotelMy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                    fragMap.getMapAsync(AddHotelActivity.this);
//                    TransitionSet seta = new TransitionSet()
//                            .addTransition(new Scale(0.7f))
//                            .addTransition(new Fade())
//                            .setDuration(500)
//                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
//                    TransitionManager.beginDelayedTransition(backview, seta);
//                    ldetHotel.setVisibility(View.VISIBLE);
//                    lvHotel.setVisibility(View.GONE);
//                    lvHotelMy.setVisibility(View.GONE);
//                    tvHeaderMy.setVisibility(View.GONE);
//                    tvHeader.setVisibility(View.GONE);
//                    tvdmNama.setText(sessionManager.getNamaHotel());
//                    tvdmTelp.setText(sessionManager.getAlamatHotel());
//                    buttonEnterFalses.setVisibility(View.GONE);
//                    sLat = sessionManager.getLatHotel();
//                    sLon = sessionManager.getLonHotel();
//                    imgdm.setTag(i);
//                    btnMy.setVisibility(View.GONE);
//                    back = 1;
//                    setTitle("Hotel Saya");
//                    invalidateOptionsMenu();
//                }
//            });
//        }
//    }

    public void AddHotel() {
        back = 99;
        // membuat Intent untuk Place Picker
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            //menjalankan place picker
            startActivityForResult(builder.build(AddHotelActivity.this), PLACE_PICKER_REQUEST);

            // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

//    public void SetMyHotel(View v) {
//        sessionManager.createMyHotel(sLat,sLon,tvdmNama.getText().toString(),tvdmTelp.getText().toString());
//        Toast.makeText(this, "Sukses mengeset Hotel", Toast.LENGTH_LONG).show();
//    }

    public void SetMyHotel(View v) {
        AddHotel();
    }

    public void SetMyHotel() {
        sessionManager.createMyHotel(sLat, sLon, sPlaceName, sPlaceAddress);
        Toast.makeText(this, "Sukses mengeset Hotel", Toast.LENGTH_LONG).show();
        InitHotel();
    }

    public void Navigator(View v) {
//        int i = (int) v.getTag();
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + sLat + "," + sLon));
//        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();
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
                    .title("Hotel")
                    .position(latlng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hotels)
                    ));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerJemaahA.getPosition(), 17f));
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

    public void DoRemoveHotel(View v) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Apakah Anda yakin ingin menghapus Hotel ini?");
        // Ini yang kemaren error.. karena depannya tidak dikasih builder.
        // sebelumnya :
        // setCancelable(false);
        builder.setCancelable(false);
        builder.setPositiveButton("YA", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeleteHotel();
                    }
                });
        builder.setNegativeButton("Tidak", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                }).show();
    }

    public void DeleteHotel() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("hotel_id", sId[pos]);
        StringEntity entity = null;

        try {
            entity = new StringEntity(requestParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(this, Constant.DeleteHotel + sToken, entity, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d(TAG, "onSuccess: " + String.valueOf(response));

                try {
                    jResponse = response.getJSONObject(Constant.RESPONSE);
                    status = jResponse.getBoolean(Constant.STATUS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status) {
                    Toast.makeText(AddHotelActivity.this, "Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                    ListHotel();
                    onBackPressed();
                } else {
                    Toast.makeText(AddHotelActivity.this, "Coba lagi.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "onFailure: " + responseString );
                Toast.makeText(AddHotelActivity.this, "Koneksi Anda bermasalah silahkan coba lagi.", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
