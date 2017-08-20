package com.huji.apps.haji.petugas;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.huji.apps.haji.petugas.Adapters.CustomListViewBCAdapter;
import com.huji.apps.haji.petugas.Adapters.CustomListViewLJAdapter;
import com.huji.apps.haji.petugas.Beans.RowItemBc;
import com.huji.apps.haji.petugas.Beans.RowItemLJ;
import com.huji.apps.haji.petugas.Receiver.GpsTrackerAlarmReceiver;
import com.huji.apps.haji.petugas.Services.FetchAddressIntentService;
import com.huji.apps.haji.petugas.Utils.Constant;
import com.huji.apps.haji.petugas.Utils.LoopjHttpClient;
import com.huji.apps.haji.petugas.Utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.transitionseverywhere.TransitionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static com.huji.apps.haji.petugas.Utils.Constant.POSITION;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, ResultCallback<LocationSettingsResult>, SearchView.OnQueryTextListener {


    String[] kuids, kloter_id, embarkasi_id, rom_ids, group_ids;
    String kuid = "0", rom_id = "0", group_id = "0";
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
    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    private static final String TAG = "MapsActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    /**
     * Provides the entry point to Google Play services.
     */
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
    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    protected Boolean mRequestingLocationUpdates = false;
    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    /**
     * The formatted location address.
     */
    protected String mAddressOutput;
    GoogleApiClient mGoogleApiClientLogin;
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    CustomListViewLJAdapter adapter;
    // TODO

    GoogleMap map;
    GoogleMapOptions options = new GoogleMapOptions();
    SupportMapFragment fragMap;
    // Todo
    SessionManager session;
    boolean help_state;
    String jumID, sLongitude, sLatitude, sTokens, sHelp, sType, sFilter, sUTC;
    int backs = 0, updateval = 0, stateklik = 2;
    String[] sNamaJemaah, sPhotoJemaah, sTeleponJemaah, sNamaPetugas, sPhotoPetugas, sTeleponPetugas, sDatetimeJ, sDatetimeP,
            sPhotoJemaah32, sPhotoJemaah200, sPhotoPetugas32, sPhotoPetugas200;

    TextView tvdmNama, tvdmTelp, tvTipe, tvLast;
    ImageView imgdm, imgnav;
    View mapView, vBcNot;
    String[] sNama = new String[0], sImg, sTel, sLJLat, sLJLon, sLJTime, sLatJ, sLonJ, sLatP, sLonP,
            sImageBC, sImgFull, sNamaBC = new String[0], sTimeBC, sJudulBC, sIsiBC, sImgPBC, sTypes;
    Boolean[] sStatus;
    JSONObject jResponse = new JSONObject(), jData = new JSONObject();
    JSONArray jAJemaah = new JSONArray(), jAPetugas = new JSONArray(), jAData = new JSONArray();
    View vMarkerDetail, vLoadMap, vNotMap, lListJemaah, lLokasi, lBroadcast, lHelp, vHelpSearch, vLjNo;
    ViewGroup backview;
    // before loop:
    List<Marker> markerJemaah = new ArrayList<>(), markerPetugas;
    Marker youmarker;
    Marker[] markJ, markP;
    Button btnMan, btnLok, btnBrod, btnHelp;
    Switch locationbutton;
    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    boolean mAddressRequested = true;
    int tracks = 3, iMarkClicks = 0, iMarkJ, iMarkP;
    LatLng latLngClick;
    android.support.v7.app.ActionBar actionBar;
    AlertDialog.Builder dialogBuilder;
    Context context;
    ArrayList<RowItemLJ> rowItemLjs = new ArrayList<>();
    ListView lvLj;
    List<RowItemBc> rowItemBcs = new ArrayList<>();
    ListView lvBc;
    SearchView mSearchView;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = this;

        actionBar = getSupportActionBar();
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClientLogin = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        fragMap = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (checkPlayServices()) {
            // Kick off the process of building the GoogleApiClient, LocationRequest, and
            // LocationSettingsRequest objects.
            buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();
            checkLocationSettings();

            fragMap.getMapAsync(this);
        }

        mapView = fragMap.getView();
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 0, 42);

        // Session class instance
        session = new SessionManager(getApplicationContext());
        jumID = session.getIds();
        sTokens = session.getToken();

        Log.w(TAG, "onCreate: \n" +
                "ID : " + jumID + "\n" +
                "TK : " + sTokens);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("done") != null) {
                session.createHelp("kosong");
            }
        }
        help_state = session.isHelps();
        Log.e(TAG, "onCreate: HelpState " + help_state);
        if (help_state) {
            Intent a = new Intent(MapsActivity.this, HelpActivity.class);
            a.putExtra("help", "kehelp");
            startActivity(a);
            finish();
        }
//        sHelp = session.getHelp();
//        Log.w(TAG, "onCreate: " + sHelp);
//        if (sHelp.equalsIgnoreCase("help")) {
//            Intent a = new Intent(MapsActivity.this, HelpActivity.class);
//            a.putExtra("help", "kehelp");
//            startActivity(a);
//            finish();
//            return;
//        } else if (sHelp.equalsIgnoreCase("helpr")) {
//            Intent a = new Intent(MapsActivity.this, HelpActivity.class);
//            a.putExtra("helpr", "dohelp");
//            startActivity(a);
//            finish();
//            return;
//        }

        vMarkerDetail = findViewById(R.id.detailmarker);
        vLoadMap = findViewById(R.id.loadmap);
        vNotMap = findViewById(R.id.mapnot);
//        vHelpSearch = findViewById(R.id.HelpSearchView);

        mSearchView = (SearchView) findViewById(R.id.searchView1);

        lListJemaah = findViewById(R.id.lListJemaah);
        lLokasi = findViewById(R.id.lLokasi);
        lBroadcast = findViewById(R.id.lBroadcast);
        lHelp = findViewById(R.id.lLain);

        lLokasi.setVisibility(View.GONE);

        backview = (ViewGroup) findViewById(R.id.MainsMaps);

        tvdmNama = (TextView) findViewById(R.id.tvdmNama);
        tvdmTelp = (TextView) findViewById(R.id.tvdmTelp);

        tvTipe = (TextView) findViewById(R.id.tvtipemarker);
        tvLast = (TextView) findViewById(R.id.tvlastupdate);

        imgdm = (ImageView) findViewById(R.id.iconsmarker);
        imgdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MapsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.fullimg);
                dialog.setCancelable(true);
                dialog.show();
                ImageView zoom = (ImageView) dialog.findViewById(R.id.imgzoom);
                if (iMarkClicks == 1) {
                    Picasso.with(MapsActivity.this)
                            .load(sPhotoJemaah[iMarkJ])
                            .placeholder(R.drawable.lod)
                            .into(zoom);
                } else if (iMarkClicks == 2) {
                    Picasso.with(MapsActivity.this)
                            .load(sPhotoPetugas[iMarkP])
                            .placeholder(R.drawable.lod)
                            .into(zoom);
                }
//                zoom.setImageDrawable(imgdm.getDrawable());
                zoom.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();

                    }
                });
            }
        });

        imgnav = (ImageView) findViewById(R.id.navimg);

        btnMan = (Button) findViewById(R.id.btnmanasik);
        btnMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tracks == 3 && stateklik == 2) {
                    KananKanan(vLoadMap, lListJemaah);

                } else if (tracks == 1 && stateklik == 2) {
                    KananKanan(lLokasi, lListJemaah);

                } else if (tracks == 0 && stateklik == 2) {
                    KananKanan(vNotMap, lListJemaah);

                } else if (stateklik == 3) {
                    KananKanan(lBroadcast, lListJemaah);

                } else if (stateklik == 4) {
                    KananKanan(lHelp, lListJemaah);

                }
                stateklik = 1;
                Log.i(TAG, "onClick: " + String.valueOf(stateklik));
                btnMan.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                btnLok.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnBrod.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnHelp.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                GetListJemaah();
                invalidateOptionsMenu();
                backs = 10;

            }
        });
        btnLok = (Button) findViewById(R.id.btnlokasi);
        btnLok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tracks == 3) {
                    if (stateklik == 1) {
                        KiriKiri(lListJemaah, vLoadMap);
                    } else if (stateklik == 3) {
                        KananKanan(lBroadcast, vLoadMap);
                    } else if (stateklik == 4) {
                        KananKanan(lHelp, vLoadMap);
                    }
                } else if (tracks == 1) {
                    if (stateklik == 1) {
                        KiriKiri(lListJemaah, lLokasi);
                    } else if (stateklik == 3) {
                        KananKanan(lBroadcast, lLokasi);
                    } else if (stateklik == 4) {
                        KananKanan(lHelp, lLokasi);
                    }
                } else if (tracks == 0) {
                    if (stateklik == 1) {
                        KiriKiri(lListJemaah, vNotMap);
                    } else if (stateklik == 3) {
                        KananKanan(lBroadcast, vNotMap);
                    } else if (stateklik == 4) {
                        KananKanan(lHelp, vNotMap);
                    }
                }
                stateklik = 2;
                Log.i(TAG, "onClick: " + String.valueOf(stateklik));
                btnMan.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnLok.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                btnBrod.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnHelp.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                invalidateOptionsMenu();

            }
        });
        btnBrod = (Button) findViewById(R.id.btnbroadcast);
        btnBrod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tracks == 3 && stateklik == 2) {
                    KiriKiri(vLoadMap, lBroadcast);

                } else if (tracks == 1 && stateklik == 2) {
                    KiriKiri(lLokasi, lBroadcast);

                } else if (tracks == 0 && stateklik == 2) {
                    KiriKiri(vNotMap, lBroadcast);

                } else if (stateklik == 1) {
                    KiriKiri(lListJemaah, lBroadcast);

                } else if (stateklik == 4) {
                    KananKanan(lHelp, lBroadcast);

                }
                stateklik = 3;
                GetListBroadcast();
                Log.i(TAG, "onClick: " + String.valueOf(stateklik));
                btnMan.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnLok.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnBrod.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                btnHelp.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                invalidateOptionsMenu();

            }
        });
        btnHelp = (Button) findViewById(R.id.btnhelp);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tracks == 3 && stateklik == 2) {
                    KiriKiri(vLoadMap, lHelp);

                } else if (tracks == 1 && stateklik == 2) {
                    KiriKiri(lLokasi, lHelp);

                } else if (tracks == 0 && stateklik == 2) {
                    KiriKiri(vNotMap, lHelp);

                } else if (stateklik == 1) {
                    KiriKiri(lListJemaah, lHelp);

                } else if (stateklik == 3) {
                    KiriKiri(lBroadcast, lHelp);

                }
                stateklik = 4;
                Log.i(TAG, "onClick: " + String.valueOf(stateklik));
                btnMan.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnLok.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnBrod.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnHelp.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                invalidateOptionsMenu();
            }
        });

        mResultReceiver = new AddressResultReceiver(new Handler());
        dialogBuilder = new AlertDialog.Builder(this);

        vBcNot = findViewById(R.id.lBroadcastNo);
        lvBc = (ListView) findViewById(R.id.lvBc);

        lvLj = (ListView) findViewById(R.id.lvLj);
        vLjNo = findViewById(R.id.lListEmpty);


        sType = session.getType();

        IsiFilter();

        CheckData();

        Log.e(TAG, "onCreate: Type Petugas "+sType );

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");
        sUTC = date.format(currentLocalTime);
        sUTC = sUTC.replaceAll("\\+","pls");
        sUTC = sUTC.replaceAll("\\-","mns");
        Log.e(TAG, "onCreate: UTC " + sUTC );

    }

    void IsiFilter(){
        if ((sType.equalsIgnoreCase("ketua kloter") || sType.equalsIgnoreCase("bimbingan ibadah") ||
                sType.equalsIgnoreCase("perawat") || sType.equalsIgnoreCase("dokter"))){
            sFilter = "kloter";
        } else if (sType.equalsIgnoreCase("ketua rombongan")){
            sFilter = "rombongan";
        } else if ((sType.equalsIgnoreCase("ketua regu"))){
            sFilter = "group";
        }
    }

    void CheckData() {
        if (session.isKuid_DATA().equalsIgnoreCase("0")) {
            getListEmbarkasi();
        } else {
            if (session.isKuid().equalsIgnoreCase("0") ) {
                ShowEmbarkasi();
            } else if (session.isRom_DATA().equalsIgnoreCase("0")) {
                getListRombongan();
            } else {
                if (session.isRom().equalsIgnoreCase("0") && sType.equalsIgnoreCase("ketua rombongan")) {
                    ShowRombongan();
                } else if (session.isGroup_DATA().equalsIgnoreCase("0")) {
                    getListGroup();
                } else {
                    if (session.isGroup().equalsIgnoreCase("0") && sType.equalsIgnoreCase("ketua regu")) {
                        ShowGroup();
                    } else {
                        Log.e(TAG, "CheckData: Donot 1");
                    }
                }
            }
        }
    }

    public void GetListBroadcast() {
        LoopjHttpClient.get(Constant.ListBroadcast + sTokens    , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, "onSuccess: " + String.valueOf(response));
                try {
                    jResponse = response.getJSONObject("response");
//                    if (jResponse.getBoolean("status")) {
                    if (jResponse.has("list_broadcast")) {
                        jAJemaah = jResponse.getJSONArray("list_broadcast");
                        if (jAJemaah.length() > 0) {

                            sImgPBC = new String[jAJemaah.length()];
                            sNamaBC = new String[jAJemaah.length()];
                            sTimeBC = new String[jAJemaah.length()];
                            sJudulBC = new String[jAJemaah.length()];
                            sIsiBC = new String[jAJemaah.length()];
                            sImageBC = new String[jAJemaah.length()];
                            sImgFull = new String[jAJemaah.length()];
                            for (int i = 0; i < jAJemaah.length(); i++) {
                                jData = jAJemaah.getJSONObject(i);

                                sImgPBC[i] = jData.getString("part_image");
                                sNamaBC[i] = jData.getString("part_fullname");
                                sTimeBC[i] = String.valueOf(i) + " menit yang lalu";
                                sJudulBC[i] = jData.getString("bc_title");
                                sIsiBC[i] = jData.getString("bc_message");
                                sImageBC[i] = jData.getString("bc_image_200");
                                sImgFull[i] = jData.getString("bc_image");

                                if (jData.getInt("bc_year") > 0) {
                                    sTimeBC[i] = String.valueOf(jData.getInt("bc_year")) + " tahun yang lalu";
                                } else if (jData.getInt("bc_month") > 0) {
                                    sTimeBC[i] = String.valueOf(jData.getInt("bc_month")) + " bulan yang lalu";
                                } else if (jData.getInt("bc_day") > 0) {
                                    sTimeBC[i] = String.valueOf(jData.getInt("bc_day")) + " hari yang lalu";
                                } else if (jData.getInt("bc_hour") > 0) {
                                    sTimeBC[i] = String.valueOf(jData.getInt("bc_hour")) + " jam yang lalu";
                                } else if (jData.getInt("bc_minute") > 0) {
                                    sTimeBC[i] = String.valueOf(jData.getInt("bc_minute")) + " menit yang lalu";
                                } else {
                                    sTimeBC[i] = String.valueOf(jData.getInt("bc_second")) + " detik yang lalu";
                                }
                            }
                        }
                        InitBC();

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

    public void InitBC() {
        rowItemBcs = new ArrayList<>();

        if (sNamaBC.length == 0) {
            vBcNot.setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < sNamaBC.length; i++) {
                rowItemBcs.add(new RowItemBc(sImgPBC[i], sNamaBC[i], sTimeBC[i], sJudulBC[i], sIsiBC[i], sImageBC[i]));
            }
            vBcNot.setVisibility(View.GONE);
            CustomListViewBCAdapter adapter = new CustomListViewBCAdapter(this, R.layout.item_broadcast, rowItemBcs);
            adapter.notifyDataSetChanged();
            lvBc.setAdapter(adapter);
            lvBc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent inte = new Intent(MapsActivity.this, DetailBroadcastActivity.class);
                    inte.putExtra("bcpimg", sImgPBC[i]);
                    inte.putExtra("bcnama", sNamaBC[i]);
                    inte.putExtra("bcwaktu", sTimeBC[i]);
                    inte.putExtra("bcjudul", sJudulBC[i]);
                    inte.putExtra("bcisi", sIsiBC[i]);
                    inte.putExtra("bcimg", sImageBC[i]);
                    inte.putExtra("full", sImgFull[i]);
                    startActivity(inte);
                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem itemSwitch = menu.findItem(R.id.myswitch);
        itemSwitch.setActionView(R.layout.switch_layout);
        final Switch sw = (Switch) menu.findItem(R.id.myswitch).getActionView().findViewById(R.id.switchmarker);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (Marker m : markerJemaah) {
                        m.setVisible(true);
                        Log.e(TAG, "onCheckedChanged: true ");
                    }
                    Toast.makeText(MapsActivity.this, "Jamaah terlihat", Toast.LENGTH_SHORT).show();
                } else {
                    for (Marker m : markerJemaah) {
                        m.setVisible(false);
                        Log.e(TAG, "onCheckedChanged: true ");
                    }
                    Toast.makeText(MapsActivity.this, "Jamaah disembunyikan", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (stateklik == 2) {
            itemSwitch.setVisible(true);
            menu.findItem(R.id.refresh_track).setVisible(true);
            menu.findItem(R.id.add_broadcast).setVisible(false);
        } else if (stateklik == 3) {

            itemSwitch.setVisible(false);
            menu.findItem(R.id.refresh_track).setVisible(false);
            menu.findItem(R.id.add_broadcast).setVisible(true);
        } else {

            itemSwitch.setVisible(false);
            menu.findItem(R.id.refresh_track).setVisible(false);
            menu.findItem(R.id.add_broadcast).setVisible(false);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh_track: {
                InitJemaah(mCurrentLocation);
                return true;
            }
            case R.id.add_broadcast: {
                AddBroadcast();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (backs == 1) {
            TransitionManager.beginDelayedTransition(backview);
            vMarkerDetail.setVisibility(View.GONE);
//            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngClick, 15f));
            backs = 0;
        } else if (backs == 10 || backs == 30 || backs == 40) {
            btnLok.callOnClick();
            backs = 0;
        } else {
            super.onBackPressed();
        }
    }

    public void getListEmbarkasi() {
        Log.e(TAG, "getListEmbarkasi: " + Constant.GetKloter);
        LoopjHttpClient.get(Constant.GetKloter, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e(TAG, "onSuccess: Kloter" + String.valueOf(response));
                session.CreateisKuid_DATA(String.valueOf(response));

                if (session.isKuid().equalsIgnoreCase("0")) {
                    ShowEmbarkasi();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: " + String.valueOf(responseString));
            }
        });
    }

    public void getListRombongan() {
        Log.e(TAG, "getListRombongan: " + Constant.GetRombongan);
        LoopjHttpClient.get(Constant.GetRombongan, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e(TAG, "onSuccess: Rombongan" + String.valueOf(response));
                session.CreateisRom_DATA(String.valueOf(response));

                if (session.isRom().equalsIgnoreCase("0") && (sType.equalsIgnoreCase("ketua rombongan") || sType.equalsIgnoreCase("ketua regu")) ) {
                    ShowRombongan();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: " + String.valueOf(responseString));
            }
        });
    }

    public void getListGroup() {
        Log.e(TAG, "getListGroup: " + Constant.GetGroup);
        LoopjHttpClient.get(Constant.GetGroup, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e(TAG, "onSuccess: Group" + String.valueOf(response));
                session.CreateisGroup_DATA(String.valueOf(response));

                if (session.isGroup().equalsIgnoreCase("0") && sType.equalsIgnoreCase("ketua regu")) {
                    ShowGroup();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: " + String.valueOf(responseString));
            }
        });
    }

    public void ShowEmbarkasi() {
        try {

            jAData = new JSONArray(session.isKuid_DATA());

            kuids = new String[jAData.length()];
            kloter_id = new String[jAData.length()];
            embarkasi_id = new String[jAData.length()];

            for (int i = 0; i < jAData.length(); i++) {
                jData = jAData.getJSONObject(i);
                kuids[i] = jData.getString("kuid");
                kloter_id[i] = jData.getString("kloter_id");
                embarkasi_id[i] = jData.getString("embarkasi_id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        // Title
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.headerembarkasi, null);
        dialogBuilder.setCustomTitle(view);

        // Title
        LayoutInflater inflatermse = getLayoutInflater();
        View views = inflatermse.inflate(R.layout.contentspin, null);

        final SpinnerDialog spinnerDialog;
        final TextView selectedItems = (TextView) views.findViewById(R.id.tvembar);

        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < embarkasi_id.length; i++) {
            items.add(embarkasi_id[i] + " " + kloter_id[i]);

        }

        spinnerDialog = new SpinnerDialog(this, items, "Pilih atau Cari Embarkasi");// With No Animation

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
//                Toast.makeText(this, item + "  " + position+"", Toast.LENGTH_LONG).show();
                selectedItems.setText(item);
                kuid = kuids[position];
                Log.e(TAG, "onClick: Spinner " + kuids[position]);
            }
        });
        views.findViewById(R.id.PilihanEmbarkasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });

        dialogBuilder.setView(views);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (kuid.equalsIgnoreCase("0")) {
                            Toast.makeText(MapsActivity.this, "Anda belum memilih Embarkasi", Toast.LENGTH_LONG).show();
                            ShowEmbarkasi();
                        } else {
                            SaveEmbarkasi(kuid);
                        }
                    }
                });
        dialogBuilder.setNegativeButton("Nanti", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        Toast.makeText(MapsActivity.this, "Silahkan setting di menu profile.", Toast.LENGTH_LONG).show();
                    }
                });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

    public void ShowRombongan() {
        try {

            jAData = new JSONArray(session.isRom_DATA());

            rom_ids = new String[jAData.length()];

            for (int i = 0; i < jAData.length(); i++) {
                jData = jAData.getJSONObject(i);
                rom_ids[i] = jData.getString("rom_id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        // Title
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.headerrombongan, null);
        dialogBuilder.setCustomTitle(view);

        // Title
        LayoutInflater inflatermse = getLayoutInflater();
        View views = inflatermse.inflate(R.layout.contentspinrom, null);

        final SpinnerDialog spinnerDialog;
        final TextView selectedItems = (TextView) views.findViewById(R.id.tvrom);

        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < rom_ids.length; i++) {
            items.add(rom_ids[i]);
        }

        spinnerDialog = new SpinnerDialog(this, items, "Pilih atau Cari Rombongan");// With No Animation

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
//                Toast.makeText(this, item + "  " + position+"", Toast.LENGTH_LONG).show();
                selectedItems.setText(item);
                rom_id = rom_ids[position];
                Log.e(TAG, "onClick: Spinner " + rom_ids[position]);
            }
        });
        views.findViewById(R.id.PilihanRombongan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });

        dialogBuilder.setView(views);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (rom_id.equalsIgnoreCase("0")) {
                            Toast.makeText(MapsActivity.this, "Anda belum memilih Rombongan", Toast.LENGTH_LONG).show();
                            ShowRombongan();
                        } else {
                            SaveRombongan(rom_id);
                        }
                    }
                });
        dialogBuilder.setNegativeButton("Nanti", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        Toast.makeText(MapsActivity.this, "Silahkan setting di menu profile.", Toast.LENGTH_LONG).show();
                    }
                });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

    public void ShowGroup() {
        try {

            jAData = new JSONArray(session.isGroup_DATA());

            group_ids = new String[jAData.length()];

            for (int i = 0; i < jAData.length(); i++) {
                jData = jAData.getJSONObject(i);
                group_ids[i] = jData.getString("group_id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        // Title
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.headergroup, null);
        dialogBuilder.setCustomTitle(view);

        // Title
        LayoutInflater inflatermse = getLayoutInflater();
        View views = inflatermse.inflate(R.layout.contentspingroup, null);

        final SpinnerDialog spinnerDialog;
        final TextView selectedItems = (TextView) views.findViewById(R.id.tvgroup);

        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < group_ids.length; i++) {
            items.add(group_ids[i]);
        }

        spinnerDialog = new SpinnerDialog(this, items, "Pilih atau Cari Group");// With No Animation

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
//                Toast.makeText(this, item + "  " + position+"", Toast.LENGTH_LONG).show();
                selectedItems.setText(item);
                group_id = group_ids[position];
                Log.e(TAG, "onClick: Spinner " + group_ids[position]);
            }
        });
        views.findViewById(R.id.PilihanGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });

        dialogBuilder.setView(views);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (group_id.equalsIgnoreCase("0")) {
                            Toast.makeText(MapsActivity.this, "Anda belum memilih Group", Toast.LENGTH_LONG).show();
                            ShowGroup();
                        } else {
                            SaveGroup(group_id);
                        }
                    }
                });
        dialogBuilder.setNegativeButton("Nanti", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        Toast.makeText(MapsActivity.this, "Silahkan setting di menu profile.", Toast.LENGTH_LONG).show();
                    }
                });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }


    void SaveEmbarkasi(final String a) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("kuid", a);
        StringEntity entity = null;

        try {
            entity = new StringEntity(requestParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(this, Constant.UpdateKloter + "/access_token/" + sTokens, entity, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int kode = 0;

                try {
                    jResponse = response.getJSONObject("response");
                    kode = jResponse.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (kode == 0) {
                    session.CreateisKuid(a);
                    Toast.makeText(MapsActivity.this, "Data tersimpan", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onSuccess: " + String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "onFailure: " + responseString);
                Log.i(TAG, "onFailure: Muncul dong");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "onFailure: " + throwable.toString());
                Log.i(TAG, "onFailure: Muncul dong");
            }
        });
    }

    void SaveRombongan(final String a) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("rom_id", a);
        StringEntity entity = null;

        try {
            entity = new StringEntity(requestParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(this, Constant.UpdateRombongan + "/access_token/" + sTokens, entity, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int kode = 0;

                try {
                    jResponse = response.getJSONObject("response");
                    kode = jResponse.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (kode == 0) {
                    session.CreateisRom(a);
                    Toast.makeText(MapsActivity.this, "Data tersimpan", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onSuccess: " + String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "onFailure: " + responseString);
                Log.i(TAG, "onFailure: Muncul dong");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "onFailure: " + throwable.toString());
                Log.i(TAG, "onFailure: Muncul dong");
            }
        });
    }

    void SaveGroup(final String a) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("group_id", a);
        StringEntity entity = null;

        try {
            entity = new StringEntity(requestParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(this, Constant.UpdateGroup + "/access_token/" + sTokens, entity, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int kode = 0;

                try {
                    jResponse = response.getJSONObject("response");
                    kode = jResponse.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (kode == 0) {
                    session.CreateisGroup(a);
                    Toast.makeText(MapsActivity.this, "Data tersimpan", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onSuccess: " + String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "onFailure: " + responseString);
                Log.i(TAG, "onFailure: Muncul dong");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "onFailure: " + throwable.toString());
                Log.i(TAG, "onFailure: Muncul dong");
            }
        });
    }


    public void DoLogout(View v) {
        Auth.GoogleSignInApi.signOut(mGoogleApiClientLogin).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        SendTokenFCM("Kosong" + session.getIds(), sTokens);
                    }
                });


    }

    public void SendTokenFCM(String token, String acces) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("fcm_code", token);
        StringEntity entity = null;

        try {
            entity = new StringEntity(requestParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(this, Constant.Update_FCM + acces, entity, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int kode = 0;

                try {
                    jResponse = response.getJSONObject("response");
                    kode = jResponse.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (kode == 0) {
                    session.logoutUser();
                    finish();
                    cancelAlarmManager();
                }
                Log.d(TAG, "onSuccess: " + String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "onFailure: " + responseString);

            }
        });
    }

    public void DoHotel(View v) {
        startActivity(new Intent(MapsActivity.this, AddHotelActivity.class));
    }

    public void DoProfile(View v) {
        startActivity(new Intent(MapsActivity.this, ProfileActivity.class));
    }

    public void DoResetPIN(View v) {
        startActivity(new Intent(MapsActivity.this, ResetPINActivity.class));
    }
    public void DoHelpPetugas(View v) {
        startActivity(new Intent(MapsActivity.this, ShowHelpActivity.class));
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

    public void KiriKiri(View v1, final View v2) {
        v2.setVisibility(View.VISIBLE);
        v2.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_left_in));
        v1.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_left_out));
        v1.setVisibility(View.GONE);
    }

    public void KananKanan(View v1, final View v2) {
        v2.setVisibility(View.VISIBLE);
        v2.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_right_in));
        v1.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_right_out));
        v1.setVisibility(View.GONE);
    }

    private void startAlarmManager() {
        Log.d(TAG, "startAlarmManager");

        Context context = getBaseContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        SharedPreferences sharedPreferences = this.getSharedPreferences(Constant.PrefTAG, Context.MODE_PRIVATE);
        int intervalInMinutes = sharedPreferences.getInt("intervalInMinutes", 1);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                intervalInMinutes * 20000, // 60000 = 1 minute
                pendingIntent);
    }

    private void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void GetListJemaah() {
        sType = session.getType();
        IsiFilter();
        LoopjHttpClient.get(Constant.ListJemaah + sTokens + "?filter_by=" + sFilter + "&filter_kbih=yes&user_utc="+sUTC, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, "onSuccess: " + String.valueOf(response));
                try {
                    jResponse = response.getJSONObject("response");
//                    if (jResponse.getBoolean("status")) {
                    if (jResponse.has("data")) {
                        jAJemaah = jResponse.getJSONArray("data");
                        if (jAJemaah.length() > 0) {

                            sImg = new String[jAJemaah.length()];
                            sNama = new String[jAJemaah.length()];
                            sStatus = new Boolean[jAJemaah.length()];
                            sTel = new String[jAJemaah.length()];
                            sLJLat = new String[jAJemaah.length()];
                            sLJLon = new String[jAJemaah.length()];
                            sLJTime = new String[jAJemaah.length()];
                            for (int i = 0; i < jAJemaah.length(); i++) {
                                jData = jAJemaah.getJSONObject(i);

                                sImg[i] = jData.getString("part_image");
                                sNama[i] = jData.getString("part_fullname");
                                sStatus[i] = jData.getBoolean("active");
                                sTel[i] = jData.getString("part_country_code") + jData.getString("part_callnum");
                                if (jData.isNull("latitude")) {
                                    sLJLat[i] = "nil";
                                } else {
                                    sLJLat[i] = jData.getString("latitude");
                                }
                                if (jData.isNull("longitude")) {
                                    sLJLon[i] = "nill";
                                } else {
                                    sLJLon[i] = jData.getString("longitude");
                                }
                                if (jData.isNull("last_active")) {
                                    sLJTime[i] = "nil";
                                } else {
                                    sLJTime[i] = jData.getString("last_active");
                                }

                            }
                        } else {
                            Log.e(TAG, "onSuccess: List Jemaah 0" );
                            sImg = new String[0];
                            sNama = new String[0];
                            sStatus = new Boolean[0];
                            sTel = new String[0];
                            sLJLat = new String[0];
                            sLJLon = new String[0];
                            sLJTime = new String[0];
                        }
                        InitLJ();

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

    public void InitLJ() {
        rowItemLjs = new ArrayList<>();

        if (sNama.length == 0) {
            vLjNo.setVisibility(View.VISIBLE);
            lvLj.setVisibility(View.GONE);
            lvLj.setAdapter(null);
        } else {

            for (int i = 0; i < sNama.length; i++) {
                rowItemLjs.add(new RowItemLJ(sImg[i], sNama[i], sStatus[i], sTel[i], sLJLat[i], sLJLon[i], sLJTime[i]));
            }

            vLjNo.setVisibility(View.GONE);
            lvLj.setVisibility(View.VISIBLE);
            adapter = new CustomListViewLJAdapter(this, R.layout.itemjemaah, rowItemLjs);

            lvLj.setAdapter(adapter);
            lvLj.setTextFilterEnabled(true);

            setupSearchView();

            lvLj.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent in = new Intent(MapsActivity.this, DetailJemaahActivity.class);
                    in.putExtra("nama", adapter.employeeArrayList.get(i).getsNama());
                    in.putExtra("telepon", adapter.employeeArrayList.get(i).getsTelp());
                    in.putExtra("timeup", adapter.employeeArrayList.get(i).getsTime());
                    in.putExtra("photo", adapter.employeeArrayList.get(i).getsImage());
                    in.putExtra("lat", adapter.employeeArrayList.get(i).getsLat());
                    in.putExtra("lon", adapter.employeeArrayList.get(i).getsLon());
                    startActivity(in);

                }
            });
        }
    }

    private void setupSearchView()
    {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
//        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {

        if (TextUtils.isEmpty(newText)) {
            lvLj.clearTextFilter();
        } else {
            lvLj.setFilterText(newText);
//            adapter = new CustomListViewLJAdapter(this, R.layout.itemjemaah, adapter.employeeArrayList);
//
//            lvLj.setAdapter(adapter);
        }
//        adapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    // method to set up map
    void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        options.mapType(GoogleMap.MAP_TYPE_NORMAL);
        options.compassEnabled(false);
        options.rotateGesturesEnabled(false);
        options.tiltGesturesEnabled(false);
        options.zoomControlsEnabled(false);
        SupportMapFragment.newInstance(options);

    }

    public void AddBroadcast() {
//        // Title
//        LayoutInflater inflater = getLayoutInflater();
//        View view = inflater.inflate(R.layout.headeraddbroadcast, null);
//        dialogBuilder.setCustomTitle(view);
//
//        // Message
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        View promptView = layoutInflater.inflate(R.layout.add_broadcast, null);
//        Button cancel = (Button) promptView.findViewById(R.id.buttonEnterFalses);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//            }
//        });
//        dialogBuilder.setView(promptView);
//        alertDialog = dialogBuilder.create();
//        alertDialog.show();
//        alertDialog.setCancelable(false);
        Intent o = new Intent(this, AddBroadcastActivity.class);
        startActivity(o);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        setUpMap();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick(" + latLng + ")");
        TransitionManager.beginDelayedTransition(backview);
        vMarkerDetail.setVisibility(View.GONE);
        LatLng latlngs = new LatLng(latLng.latitude, latLng.longitude);
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlngs, 15f));
        backs = 0;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        map.getUiSettings().setMapToolbarEnabled(true);
        char[] str = new char[1];
        marker.getTitle().getChars(0, 1, str, 0);
        String a = String.valueOf(str);
//        if (a.equalsIgnoreCase("J")) {
        for (int i = 0; i < markerJemaah.size(); i++) {
            if (marker.getTitle().equalsIgnoreCase(sTypes[i]+String.valueOf(i))) {
                TransitionManager.beginDelayedTransition(backview);
                vMarkerDetail.setVisibility(View.VISIBLE);
                if (backs == 0) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), map.getCameraPosition().zoom));
                }
                backs = 1;
                tvdmNama.setText(sNamaJemaah[i]);
                tvdmTelp.setText(sTeleponJemaah[i]);
                imgnav.setTag("j");
                tvTipe.setText(sTypes[i]);
                tvLast.setText("Update terakhir\n" + sDatetimeJ[i]);
                Picasso.with(this)
                        .load(sPhotoJemaah200[i])
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .placeholder(R.drawable.profilee)
                        .into(imgdm);
                latLngClick = marker.getPosition();
                iMarkJ = i;
                iMarkClicks = 1;
                return true;
            }
        }
//        } else  {
//            for (int i = 0; i < markerPetugas.size(); i++) {
//                if (marker.getTitle().equalsIgnoreCase(sTypes[i])) {
//                    TransitionManager.beginDelayedTransition(backview);
//                    vMarkerDetail.setVisibility(View.VISIBLE);
//                    if (backs == 0) {
//                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), map.getCameraPosition().zoom));
//                    }
//                    backs = 1;
//                    tvdmNama.setText(sNamaPetugas[i]);
//                    tvdmTelp.setText(sTeleponPetugas[i]);
//                    imgnav.setTag("p");
//                    tvTipe.setText(sTypes[i]);
//                    tvLast.setText("Update terakhir\n" + sDatetimeP[i]);
//                    Picasso.with(this)
//                            .load(sPhotoPetugas200[i])
//                            .networkPolicy(NetworkPolicy.NO_CACHE)
//                            .placeholder(R.drawable.profilee)
//                            .into(imgdm);
//                    latLngClick = marker.getPosition();
//                    iMarkP = i;
//                    iMarkClicks = 2;
//                    return true;
//                }
//            }
//        }

        return false;
    }

    public void HelpClick(View v) {
        actionBar.hide();
        vHelpSearch.setVisibility(View.VISIBLE);
        backs = 99;
    }

    public void InitJemaah(Location location) {
        map.clear();
        sLatitude = String.valueOf(location.getLatitude());
        sLongitude = String.valueOf(location.getLongitude());
        LoopjHttpClient.get(Constant.MyTracking + "/access_token/" + sTokens + "?latitude=" + sLatitude + "&longitude=" + sLongitude +
                "&user_utc=" + sUTC + "&filter_by=" + sFilter, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, "onSuccess: " + String.valueOf(response));
                try {
                    jResponse = response.getJSONObject("response");
                    if (jResponse.getBoolean("status")) {
                        jAJemaah = jResponse.getJSONArray("list_participant");
                        sTypes = new String[jAJemaah.length()];
                        sNamaJemaah = new String[jAJemaah.length()];
                        sPhotoJemaah = new String[jAJemaah.length()];
                        sPhotoJemaah32 = new String[jAJemaah.length()];
                        sPhotoJemaah200 = new String[jAJemaah.length()];
                        sTeleponJemaah = new String[jAJemaah.length()];
                        sDatetimeJ = new String[jAJemaah.length()];
                        sLatJ = new String[jAJemaah.length()];
                        sLonJ = new String[jAJemaah.length()];
                        markerJemaah.clear();
                        markerJemaah = new ArrayList<>();
                        markJ = new Marker[jAJemaah.length()];

//                        jAPetugas = jResponse.getJSONArray("list_participant");
//                        sNamaPetugas = new String[jAPetugas.length()];
//                        sTeleponPetugas = new String[jAPetugas.length()];
//                        sPhotoPetugas = new String[jAPetugas.length()];
//                        sPhotoPetugas32 = new String[jAPetugas.length()];
//                        sPhotoPetugas200 = new String[jAPetugas.length()];
//                        sDatetimeP = new String[jAPetugas.length()];
//                        sLatP = new String[jAPetugas.length()];
//                        sLonP = new String[jAPetugas.length()];
//                        markerPetugas = new ArrayList<>();
//                        markP = new Marker[jAPetugas.length()];

                        for (int i = 0; i < jAJemaah.length(); i++) {
                            jData = jAJemaah.getJSONObject(i);
                            sTypes[i] = jData.getString("part_type");
//                            if (jData.getString("part_type").equalsIgnoreCase("jemaah")) {

                            sNamaJemaah[i] = jData.getString("part_name");
                            sTeleponJemaah[i] = jData.getString("callnum");
                            sPhotoJemaah[i] = jData.getString("part_image");
                            sPhotoJemaah32[i] = jData.getString("part_image_32");
                            sPhotoJemaah200[i] = jData.getString("part_image_200");
                            sDatetimeJ[i] = jData.getString("datetime");
                            sLatJ[i] = jData.getString("latitude");
                            sLonJ[i] = jData.getString("longitude");
                            addMarker(jData.getString("latitude"), jData.getString("longitude"), jData.getString("part_name"), i);
//                            }
//                            else {
//
//                                sNamaPetugas[i] = jData.getString("part_name");
//                                sTeleponPetugas[i] = jData.getString("callnum");
//                                sPhotoPetugas[i] = jData.getString("part_image");
//                                sPhotoPetugas32[i] = jData.getString("part_image_32");
//                                sPhotoPetugas200[i] = jData.getString("part_image_200");
//                                sDatetimeP[i] = jData.getString("datetime");
//                                sLatP[i] = jData.getString("latitude");
//                                sLonP[i] = jData.getString("longitude");
//                                addMarkerPetugas(jData.getString("latitude"), jData.getString("longitude"), jData.getString("part_name"), i);
//                            }

                        }

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
//        LatLng la = new LatLng(location.getLatitude(), location.getLongitude());
//        UpdateMarker(la);
    }

    void addMarker(String lat, String lon, String nama, final int i) {
        double Latitude = Double.parseDouble(lat);
        double Longitude = Double.parseDouble(lon);
        LatLng latlng = new LatLng(Latitude, Longitude);
        // add marker to the map
        if (sTypes[i].equalsIgnoreCase("jemaah")) {
            markJ[i] = map.addMarker(new MarkerOptions()
                            .title(sTypes[i]+String.valueOf(i))
                            .snippet(nama)
                            .position(latlng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.markhijau))
//                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(photo)))
            );
        } else {
            markJ[i] = map.addMarker(new MarkerOptions()
                    .title(sTypes[i]+String.valueOf(i))
                    .snippet(nama)
                    .position(latlng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markmerah)
                    ));
        }

        markerJemaah.add(markJ[i]);
    }

    void addMarkerPetugas(String lat, String lon, String nama, int i) {
        double Latitude = Double.parseDouble(lat);
        double Longitude = Double.parseDouble(lon);
        LatLng latlng = new LatLng(Latitude, Longitude);
//             add marker to the map
        Marker markerJemaahA = map.addMarker(new MarkerOptions()
                .title(sTypes[i])
                .snippet(nama)
                .position(latlng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.markmerah)
                ));
        markerPetugas.add(markerJemaahA);
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
        if (v.getTag().equals("j")) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=" + sLatJ[iMarkJ] + "," + sLonJ[iMarkJ]));
//            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=" + sLatP[iMarkP] + "," + sLonP[iMarkP]));
//            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        }
    }


    // TODO TRACKER GOOLGE

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
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

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
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
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.e(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
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
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        checkLocationSettings();
                        break;
                }
                break;
        }
    }


    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
            }
        });
        showLocation(mCurrentLocation);
        InitJemaah(mCurrentLocation);
        LatLng m = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        UpdateMarker(m);
    }

    /**
     * Sets the value of the UI fields for the location latitude, longitude and last update time.
     */
    private void updateLocationUI(Location lo) {
        if (lo != null) {
            Log.e(TAG, "updateLocationUI: ");
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateLocationUI(mCurrentLocation);

//            fetchAddressButtonHandler();
            if (mCurrentLocation != null) {
                showLocation(mCurrentLocation);
                session.createLatLon(sLatitude, sLongitude);
                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                    return;
                }
                // It is possible that the user presses the button to get the address before the
                // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                // is set to true, but no attempt is made to fetch the address (see
                // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                // user has requested an address, since we now have a connection to GoogleApiClient.
                if (mAddressRequested) {
                    startIntentService();
                    Log.w(TAG, "onConnected: Start Address");
                }
            }
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged [" + location + "]");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateLocationUI(mCurrentLocation);
        showLocation(mCurrentLocation);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    // method to show petugas n jemaah location on map
    public void showLocation(Location location) {
        updateval++;
        Log.e(TAG, "showLocation: Masuk Sini Mas Update ke " + String.valueOf(updateval));
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        sendLocationDataToWebsite(location);
        sLatitude = String.valueOf(location.getLatitude());
        sLongitude = String.valueOf(location.getLongitude());
        session.createLatLon(sLatitude, sLongitude);
        if (youmarker != null) {
            LatLng l = youmarker.getPosition();
            Log.w(TAG, "showLocation: Marker " +
                    "\nLatl :" + String.valueOf(location.getLatitude()) +
                    "\nLatm :" + String.valueOf(l.latitude) +
                    "\nLonl :" + String.valueOf(location.getLongitude()) +
                    "\nLonm :" + String.valueOf(l.longitude));

            double a = -6.2136209, b = 106.778878, c = -6.2147175, d = 106.7700922;
            LatLng tes1 = new LatLng(a, b), tes2 = new LatLng(c, d);
            double db = CalculationByDistance(l, latlng),
                    ad = CalculationByDistance(tes1, tes2);
            Log.w(TAG, "showLocation: Distance 1 :" + String.valueOf(db));
            Log.w(TAG, "showLocation: Distance 2 :" + String.valueOf(ad));
//            Toast.makeText(this, "Dis "+String.valueOf(db), Toast.LENGTH_SHORT).show();
            if (db > 0.15) {
                Log.w(TAG, "showLocation: Update Marker Mas");
                youmarker.remove();
                UpdateMarker(latlng);
                sendLocationDataToWebsite(location);
                Toast.makeText(this, "Update", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void UpdateMarker(LatLng latlng) {
        Log.w(TAG, "UpdateMarker: ");
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f));
//        youmarker = map.addMarker(new MarkerOptions()
//                .title("Kamu ada disini")
//                .position(latlng)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.biru)));
    }


    public void sendLocationDataToWebsite(Location location) {

        sLatitude = String.valueOf(location.getLatitude());
        sLongitude = String.valueOf(location.getLongitude());
        LoopjHttpClient.get(Constant.PushLocation + "/access_token/" + sTokens + "?latitude=" + sLatitude + "&longitude=" + sLongitude, new JsonHttpResponseHandler() {
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

    /**
     * calculates the distance between two locations in MILES
     */
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
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

        return Radius * c;
    }

    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
//        if (mAddressRequested) {
//            vLoadMap.setVisibility(View.VISIBLE);
//        } else {
//            vLoadMap.setVisibility(View.GONE);
//            lLokasi.setVisibility(View.VISIBLE);
//        }

        // if Saudi Arabia Else Indonesia
        if (mAddressOutput.equals(POSITION) || mAddressOutput.equals("Saudi Arabia")) {
            vLoadMap.setVisibility(View.GONE);
            lLokasi.setVisibility(View.VISIBLE);
            vNotMap.setVisibility(View.GONE);
            tracks = 1;
            // Start TRACK
            startAlarmManager();
        } else {
            vLoadMap.setVisibility(View.GONE);
            lLokasi.setVisibility(View.GONE);
            vNotMap.setVisibility(View.VISIBLE);
            cancelAlarmManager();
            stopLocationUpdates();
            tracks = 0;
        }
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        Log.w(TAG, "displayAddressOutput: \n" + mAddressOutput);
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constant.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constant.LOCATION_DATA_EXTRA, mCurrentLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }


    /**
     * Runs when user clicks the Fetch Address button. Starts the service to fetch the address if
     * GoogleApiClient is connected.
     */
    public void fetchAddressButtonHandler() {
        // We only start the service to fetch the address if GoogleApiClient is connected.
        if (mGoogleApiClient.isConnected() && mCurrentLocation != null) {
            startIntentService();
        }
        // If GoogleApiClient isn't connected, we process the user's request by setting
        // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
        // fetch the address. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUIWidgets();
    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }



    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constant.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constant.SUCCESS_RESULT) {
                Log.w(TAG, "onReceiveResult: Succes");
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }


}

