package com.huji.apps.haji.petugas;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;
import com.huji.apps.haji.petugas.Receiver.GpsTrackerAlarmReceiver;
import com.huji.apps.haji.petugas.Utils.Config;
import com.huji.apps.haji.petugas.Utils.Constant;
import com.huji.apps.haji.petugas.Utils.LoopjHttpClient;
import com.huji.apps.haji.petugas.Utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import fr.ganfra.materialspinner.MaterialSpinner;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private final static int ALL_PERMISSIONS_RESULT = 107;
    //Creating a broadcast receiver for gcm registration
    public BroadcastReceiver mRegistrationBroadcastReceiver;

    int code = 0;
    View loginAwal, loginView, loadingView, login3View, login4View;
    ViewGroup backView;
    CountryCodePicker ccp;
    TextView btnMasuk;
    ImageView imgLogo, imgAkun;
    TextInputLayout tilemail, tilkodeaktifasi, tilkodepin, etPin, etPin2, tilnama,tilemailp;
    CheckBox checkBox;
    EditText etPhone;
    int step = 0;
    boolean visible = true, vis, cek1 = false;
    JSONObject jresponse = new JSONObject(), jdata = new JSONObject();
    Bitmap myBitmap;
    Uri picUri;
    String b64, tokenfcm, ccode, sEmail, sIMEI;
    String pins = "a", type_petugas = "0";
    int kode = 0;
    String part_id, part_fullname, token = null, part_phone;
    // Session Manager Class
    SessionManager session;
    String[] ITEMS = {"ketua kloter","bimbingan ibadah","perawat","dokter","ketua rombongan","ketua regu"};
    MaterialSpinner spinner;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    String EMAIL_PATTERNs = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERNs);

    private static final String EMAIL_PATTERN2 = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+.[a-z]+";
    private Pattern pattern2 = Pattern.compile(EMAIL_PATTERN2);

    private Matcher matcher,matcher2;

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validateEmail2(String email) {
        matcher2 = pattern2.matcher(email);
        return matcher2.matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }

//        // Session class instance
//        session = new SessionManager(getApplicationContext());
//
//        // Redirect to Maps
//        session.checkLogin();
//        if (session.isLoggedIn())
//            finish();
//
//        loginView = findViewById(R.id.firstlogin);
//        loadingView = findViewById(R.id.loadingView);
//
//        backView = (ViewGroup) findViewById(R.id.activity_mains);
//
////        // [START configure_signin]
////        // Configure sign-in to request the user's ID, email address, and basic
////        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
////        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
////                .requestEmail()
////                .build();
////        // [END configure_signin]
////
////        // [START build_client]
////        // Build a GoogleApiClient with access to the Google Sign-In API and the
////        // options specified by gso.
////        mGoogleApiClient = new GoogleApiClient.Builder(this)
////                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
////                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
////                .build();
////        // [END build_client]
////
////        //Initializing our broadcast receiver
////        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
////            @Override
////            public void onReceive(Context context, Intent intent) {
////
////                // checking for type intent filter
////                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
////                    // gcm successfully registered
////                    // now subscribe to `global` topic to receive app wide notifications
////                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
////
//////                    displayFirebaseRegId();
//////                }
////                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
//////                    // new push notification is received
//////                    UpdateNotif();
//////                    txtMessage.setText(message);
////                }
////            }
////        };
//
//        displayFirebaseRegId();
//        permissions.add(CAMERA);
//        permissions.add(ACCESS_FINE_LOCATION);
//        permissions.add(CALL_PHONE);
//        permissions.add(SEND_SMS);
//        permissions.add(READ_EXTERNAL_STORAGE);
//        permissionsToRequest = findUnAskedPermissions(permissions);
//        //get the permissions we have asked for before but are not granted..
//        //we will store this in a global list to access later.
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (permissionsToRequest.size() > 0)
//                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
//        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (MaterialSpinner) findViewById(R.id.spintype);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                if (position >= 0) {
                    type_petugas = selectedItemText;
                    Log.d("Sel", "1|" + type_petugas + "|1");
                    spinner.setError(null);
                } else {
                    type_petugas = "0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loginAwal = findViewById(R.id.llAwal);
        loginView = findViewById(R.id.firstlogin);
        login3View = findViewById(R.id.thirdlogin);
        login4View = findViewById(R.id.fourthlogin);
        loadingView = findViewById(R.id.loadingView);

        backView = (ViewGroup) findViewById(R.id.activity_mains);

        btnMasuk = (TextView) findViewById(R.id.buttonnext1);

        tilemail = (TextInputLayout) findViewById(R.id.usernameWrapper);
        tilkodeaktifasi = (TextInputLayout) findViewById(R.id.kodeWrapper);
        tilkodepin = (TextInputLayout) findViewById(R.id.pinWrapper);
        etPin = (TextInputLayout) findViewById(R.id.etPin1);
        etPin2 = (TextInputLayout) findViewById(R.id.etPin2);
        tilnama = (TextInputLayout) findViewById(R.id.nameWrapper);
        tilemailp = (TextInputLayout) findViewById(R.id.emailpWrapper);

        etPhone = (EditText) findViewById(R.id.editText_phones);

        imgLogo = (ImageView) findViewById(R.id.imagelogo);
        imgAkun = (ImageView) findViewById(R.id.icons);

        radioSexGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        CloseError(tilemail);
        CloseError(tilkodeaktifasi);
        CloseError(tilkodepin);
        CloseError(etPin);
        CloseError(etPin2);
        CloseError(tilnama);
        CloseError(tilemailp);

        CloseErrorEt(etPhone);


        imgAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });

        BitmapDrawable drawable = (BitmapDrawable) imgAkun.getDrawable();
        myBitmap = drawable.getBitmap();


        // Session class instance
        session = new SessionManager(getApplicationContext());

        Log.w(TAG, "onCreate: TOKEN FCM " + session.getTokenFCM());

        // Redirect to Maps
        session.checkLogin();
        if (session.isLoggedIn()) {
            finish();
            return;
        }

        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();
//                }
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
//                    // new push notification is received
//                    UpdateNotif();
//                    txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();
        ccode = ccp.getSelectedCountryCodeWithPlus();
        Log.e(TAG, "CODe " + ccode + "|");

        permissions.add(CAMERA);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(CALL_PHONE);
        permissions.add(SEND_SMS);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(READ_PHONE_STATE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            else {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                session.CreateIMEI(telephonyManager.getDeviceId());
                Log.w(TAG, "onCreate: IMEI M: " + telephonyManager.getDeviceId());
            }
        } else {

            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            session.CreateIMEI(telephonyManager.getDeviceId());
            Log.w(TAG, "onCreate: IMEI : "+telephonyManager.getDeviceId());
        }

        sIMEI = session.getImei();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        tokenfcm = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Log.w(TAG, "displayFirebaseRegId: " + "Firebase Reg Id: " + regId);
        else
            Log.w(TAG, "displayFirebaseRegId: " + "Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    public void onBackPressed() {
        if (step == 1) {
            TransitionSet seta = new TransitionSet()
                    .addTransition(new Scale(0.7f))
                    .addTransition(new Fade())
                    .setDuration(500)
                    .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
            TransitionManager.beginDelayedTransition(backView, seta);
            loginView.setVisibility(View.GONE);
            loginAwal.setVisibility(View.VISIBLE);
            step = 0;
        } else if (step == 2) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Batal Pembuatan Akun");
            builder.setMessage("Apakah Anda yakin ingin membatalkan pembuatan akun ini?");
            // Ini yang kemaren error.. karena depannya tidak dikasih builder.
            // sebelumnya :
            // setCancelable(false);
            builder.setCancelable(false);
            builder.setPositiveButton("Ya", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DoRelog();
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
        } else {
            super.onBackPressed();
        }
    }

    public void CloseError(final TextInputLayout v) {
        v.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable edt) {
                if (v.getEditText().getText().length() > 0) {
                    v.setError(null);
                    v.setErrorEnabled(false);
                }
            }
        });
    }

    public void CloseErrorEt(final EditText v) {
        v.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable edt) {
                if (v.getText().length() > 0) {
                    v.setError(null);
                }
            }
        });
    }

    public void DoClick(View view) {
        signIn();
    }

    @Override
    public void onStart() {
        super.onStart();

//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//            // and the GoogleSignInResult will be available instantly.
//            Log.d(TAG, "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//            // If the user has not previously signed in on this device or the sign-in has expired,
//            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
//            // single sign-on will occur in this branch.
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
    }

//    // [START onActivityResult]
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//        }
//    }

    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            sEmail = acct.getEmail();
//            DoLoginGmail();
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
//            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
//                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
//                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public void ClickLogin(View v) {

        step = 1;
        Dol(2);
    }

    public void ClickRegister(View v) {
        step = 1;
        Dol(1);
    }

    @Override
    public void onClick(View view) {

    }

//    public void DoLoginGmail() {
//        RequestParams requestParams = new RequestParams();
//        requestParams.put("email", sEmail);
//        StringEntity entity = null;
//
//        try {
//            entity = new StringEntity(requestParams.toString());
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        LoopjHttpClient.post(this, Constant.LoginPetugas, entity, new JsonHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//                TransitionSet seta = new TransitionSet()
//                        .addTransition(new Scale(0.7f))
//                        .addTransition(new Fade())
//                        .setDuration(500)
//                        .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
//                TransitionManager.beginDelayedTransition(backView, seta);
//                loadingView.setVisibility(View.VISIBLE);
//                loginView.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.d(TAG, "onSuccess: " + String.valueOf(response));
//                try {
//                    jresponse = response.getJSONObject("response");
//                    if (jresponse.has("access_token")) {
//                        token = jresponse.getString("access_token");
//                    }
//                    code = jresponse.getInt("code");
//                    if (jresponse.getBoolean("status")) {
//                        jdata = jresponse.getJSONObject("data");
//                        part_id = jdata.getString("jum_id");
//                        part_fullname = jdata.getString("jum_fullname");
//                        part_phone = jdata.getString("jum_country_code") + jdata.getString("jum_callnum");
//                        if (code == 100) {
//                            displayFirebaseRegId();
//                            SendTokenFCM(tokenfcm, token);
//                        } else {
//                            loadingView.setVisibility(View.GONE);
//                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//                            builder.setMessage("Akun Anda tidak terdaftar.");
//                            builder.setCancelable(false);
//                            builder.setPositiveButton("OK", new
//                                    DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            signOut();
//                                            loginView.setVisibility(View.VISIBLE);
//                                            dialog.cancel();
//                                        }
//                                    }).show();
//                        }
//                    } else {
//                        loadingView.setVisibility(View.GONE);
//                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//                        builder.setMessage("Akun Anda tidak terdaftar.");
//                        builder.setCancelable(false);
//                        builder.setPositiveButton("OK", new
//                                DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        signOut();
//                                        loginView.setVisibility(View.VISIBLE);
//                                        dialog.cancel();
//                                    }
//                                }).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//                Log.e(TAG, "onFailure: " + responseString);
//                loadingView.setVisibility(View.GONE);
//                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//                builder.setMessage("Koneksi Anda bermasalah, silahkan coba lagi.");
//                builder.setCancelable(false);
//                builder.setPositiveButton("OK", new
//                        DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                signOut();
//                                loginView.setVisibility(View.VISIBLE);
//                                dialog.cancel();
//                            }
//                        }).show();
//            }
//        });
//    }

    public void DoRelog() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void Dol(int a) {
        sIMEI = session.getImei();
        if (a == 1) {
            vis = true;
            TransitionSet seta = new TransitionSet()
                    .addTransition(new Scale(0.7f))
                    .addTransition(new Fade())
                    .setDuration(500)
                    .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
            TransitionManager.beginDelayedTransition(backView, seta);
            loginView.setVisibility(View.VISIBLE);
            loginAwal.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
//            tilkodeaktifasi.setVisibility(View.VISIBLE);
            tilkodepin.setVisibility(View.GONE);
        } else {
            vis = false;
            TransitionSet seta = new TransitionSet()
                    .addTransition(new Scale(0.7f))
                    .addTransition(new Fade())
                    .setDuration(500)
                    .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
            TransitionManager.beginDelayedTransition(backView, seta);
            loginView.setVisibility(View.VISIBLE);
            loginAwal.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            tilkodeaktifasi.setVisibility(View.GONE);
            tilkodepin.setVisibility(View.VISIBLE);
        }

    }

    public void FirstDo(View v) {
        sIMEI = session.getImei();
        if (vis) {
            if (tilemail.getEditText().length() == 0) {
                Snackbar.make(findViewById(android.R.id.content), "Input passport Anda.", Snackbar.LENGTH_LONG)
                        .show();
                tilemail.getEditText().requestFocus();
                tilemail.setError("Input passport Anda");

            } else if (tilkodeaktifasi.getEditText().length() == 0) {
                Snackbar.make(findViewById(android.R.id.content), "Input kode aktifasi.", Snackbar.LENGTH_LONG)
                        .show();
                tilkodeaktifasi.getEditText().requestFocus();
                tilkodeaktifasi.setError("Input kode aktifasi");
            } else {
                RequestParams requestParams = new RequestParams();
                requestParams.put("passport", tilemail.getEditText().getText().toString());
                requestParams.put("activation_code", tilkodeaktifasi.getEditText().getText().toString());
                requestParams.put("app_type", Constant.APP_TYPE);
                StringEntity entity = null;

                try {
                    entity = new StringEntity(requestParams.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                LoopjHttpClient.post(this, Constant.InitData, entity, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        TransitionSet seta = new TransitionSet()
                                .addTransition(new Scale(0.7f))
                                .addTransition(new Fade())
                                .setDuration(500)
                                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                        TransitionManager.beginDelayedTransition(backView, seta);
                        loadingView.setVisibility(View.VISIBLE);
                        loginView.setVisibility(View.GONE);
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

                        if (kode == 101) {
                            TransitionSet seta = new TransitionSet()
                                    .addTransition(new Scale(0.7f))
                                    .addTransition(new Fade())
                                    .setDuration(500)
                                    .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                            TransitionManager.beginDelayedTransition(backView, seta);
                            loginView.setVisibility(View.GONE);
                            loadingView.setVisibility(View.VISIBLE);

                            new android.os.Handler().postDelayed(new Runnable() {
                                @SuppressLint("NewApi")
                                @Override
                                public void run() {
                                    TransitionSet seta = new TransitionSet()
                                            .addTransition(new Scale(0.7f))
                                            .addTransition(new Fade())
                                            .setDuration(500)
                                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                                    TransitionManager.beginDelayedTransition(backView, seta);
                                    loadingView.setVisibility(View.GONE);
                                    login3View.setVisibility(View.VISIBLE);
                                }
                            }, 500);
                            step = 2;
                        } else if (kode == -100) {
                            tilkodeaktifasi.setError("Kode aktifasi Anda salah");
                            tilkodeaktifasi.requestFocus();
                            ShowHideOne(backView, loadingView, loginView, true, "Kode aktifasi Anda Salah");
                        } else if (kode == 103) {
                            ShowHideOne(backView, loadingView, loginView, true, "Silahkan masuk dengan Kode PIN Anda");
                        } else if (kode == 102) {
                            TransitionSet seta = new TransitionSet()
                                    .addTransition(new Scale(0.7f))
                                    .addTransition(new Fade())
                                    .setDuration(500)
                                    .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                            TransitionManager.beginDelayedTransition(backView, seta);
                            login3View.setVisibility(View.GONE);
                            loadingView.setVisibility(View.VISIBLE);

                            new android.os.Handler().postDelayed(new Runnable() {
                                @SuppressLint("NewApi")
                                @Override
                                public void run() {
                                    TransitionSet seta = new TransitionSet()
                                            .addTransition(new Scale(0.7f))
                                            .addTransition(new Fade())
                                            .setDuration(500)
                                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                                    TransitionManager.beginDelayedTransition(backView, seta);
                                    loadingView.setVisibility(View.GONE);
                                    login4View.setVisibility(View.VISIBLE);
                                    imgLogo.setVisibility(View.GONE);
                                }
                            }, 500);
                            step = 2;
                        }
                        Log.d(TAG, "onSuccess: " + String.valueOf(response));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
            }
        } else {
            FirstDoPIN(v);
        }
    }

    public void FirstDoPIN(final View v) {
        Log.w(TAG, "FirstDoPIN: Clicked");
        if (tilemail.getEditText().length() == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Input passport Anda.", Snackbar.LENGTH_LONG)
                    .show();
            tilemail.getEditText().requestFocus();
            tilemail.setError("Input passport Anda");

        } else if (tilkodepin.getEditText().length() == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Input kode PIN.", Snackbar.LENGTH_LONG)
                    .show();
            tilkodepin.getEditText().requestFocus();
            tilkodepin.setError("Input kode PIN");
        } else {

            sIMEI = session.getImei();
            pins = tilkodepin.getEditText().getText().toString();
            RequestParams requestParams = new RequestParams();
            requestParams.put("passport", tilemail.getEditText().getText().toString());
            requestParams.put("pin", pins);
            requestParams.put("app_type", Constant.APP_TYPE);
            requestParams.put("imei",sIMEI);
            StringEntity entity = null;

            try {
                entity = new StringEntity(requestParams.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            LoopjHttpClient.post(this, Constant.InitPIN, entity, new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    TransitionSet seta = new TransitionSet()
                            .addTransition(new Scale(0.7f))
                            .addTransition(new Fade())
                            .setDuration(500)
                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                    TransitionManager.beginDelayedTransition(backView, seta);
                    loadingView.setVisibility(View.VISIBLE);
                    loginView.setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        jresponse = response.getJSONObject("response");
                        if (jresponse.has("access_token")) {
                            token = jresponse.getString("access_token");
                        }
                        kode = jresponse.getInt("code");
                        if (jresponse.has("data")) {
                            jdata = jresponse.getJSONObject("data");
                            Log.e(TAG, "onSuccess: Data " + String.valueOf(jdata.toString()));
                            part_id = jdata.getString("part_id");
                            part_fullname = jdata.getString("part_fullname");
                            part_phone = jdata.getString("part_country_code") + jdata.getString("part_callnum");
                            if (!jdata.isNull("kuid")) {
                                session.CreateisKuid(jdata.getString("kuid"));
                            }

                            if (!jdata.isNull("rom_id")) {
                                session.CreateisRom(jdata.getString("rom_id"));
                            }

                            if (!jdata.isNull("group_id")) {
                                session.CreateisGroup(jdata.getString("group_id"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (kode == 100) {

                        displayFirebaseRegId();
                        SendTokenFCM(tokenfcm, token);

                    } else if (kode == 501) {

                        tilemail.getEditText().requestFocus();
                        tilemail.setError("Passport Anda Salah");
                        ShowHideOne(backView, loadingView, loginView, true, "Passport Anda Salah");
                    } else if (kode == -101) {
                        tilkodepin.getEditText().requestFocus();
                        tilkodepin.setError("Pin Anda Salah");
                        ShowHideOne(backView, loadingView, loginView, true, "PIN Anda Salah");

                    } else if (kode == 102) {
                        TransitionSet seta = new TransitionSet()
                                .addTransition(new Scale(0.7f))
                                .addTransition(new Fade())
                                .setDuration(500)
                                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                        TransitionManager.beginDelayedTransition(backView, seta);
                        login3View.setVisibility(View.GONE);
                        loadingView.setVisibility(View.VISIBLE);

                        new android.os.Handler().postDelayed(new Runnable() {
                            @SuppressLint("NewApi")
                            @Override
                            public void run() {
                                TransitionSet seta = new TransitionSet()
                                        .addTransition(new Scale(0.7f))
                                        .addTransition(new Fade())
                                        .setDuration(500)
                                        .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                                TransitionManager.beginDelayedTransition(backView, seta);
                                loadingView.setVisibility(View.GONE);
                                login4View.setVisibility(View.VISIBLE);
                                imgLogo.setVisibility(View.GONE);
                            }
                        }, 500);
                        step = 2;
                    } else if (kode == 104) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Informasi");
                        builder.setMessage("Anda telah login di perangkat lain. Apakah Anda ingin keluar dari perangkat tersebut?");
                        builder.setCancelable(false);
                        builder.setNegativeButton("Tidak", new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        // TODO Auto-generated method stub
                                        dialog.cancel();
                                        TransitionSet seta = new TransitionSet()
                                                .addTransition(new Scale(0.7f))
                                                .addTransition(new Fade())
                                                .setDuration(500)
                                                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                                        TransitionManager.beginDelayedTransition(backView, seta);
                                        loadingView.setVisibility(View.GONE);
                                        loginView.setVisibility(View.VISIBLE);
                                    }
                                });
                        builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                UpdateIMEI(token, v);
                            }
                        }).show();
                    }
                    Log.d(TAG, "onSuccess: " + String.valueOf(response));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(MainActivity.this, "Terjadi kesalahan, silahkan coba lagi. . .", Toast.LENGTH_SHORT).show();
                    TransitionSet seta = new TransitionSet()
                            .addTransition(new Scale(0.7f))
                            .addTransition(new Fade())
                            .setDuration(500)
                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                    TransitionManager.beginDelayedTransition(backView, seta);
                    loadingView.setVisibility(View.GONE);
                    loginView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e(TAG, "onFailure: " + throwable.toString());
                    Log.i(TAG, "onFailure: Muncul dong");
                    Toast.makeText(MainActivity.this, "Terjadi kesalahan, silahkan coba lagi. . .", Toast.LENGTH_SHORT).show();
                    TransitionSet seta = new TransitionSet()
                            .addTransition(new Scale(0.7f))
                            .addTransition(new Fade())
                            .setDuration(500)
                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                    TransitionManager.beginDelayedTransition(backView, seta);
                    loadingView.setVisibility(View.GONE);
                    loginView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void UpdateIMEI(String acces, final View v) {
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
                            FirstDoPIN(v);
                        }
                    }, 500);
                } else {
                    Toast.makeText(MainActivity.this, "Terjadi kesalahan, silahkan coba lagi. . .", Toast.LENGTH_SHORT).show();
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
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
                            UpdateIMEI(token, v);
                        }
                    }).show();
                }
                Log.d(TAG, "onSuccess: " + String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(MainActivity.this, "Terjadi kesalahan, silahkan coba lagi. . .", Toast.LENGTH_SHORT).show();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
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
                        UpdateIMEI(token, v);
                    }
                }).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "onFailure: " + throwable.toString());
                Log.i(TAG, "onFailure: Muncul dong");
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
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
                        UpdateIMEI(token, v);
                    }
                }).show();
            }
        });
    }

    // TODO Watch

    public void ThirdDo(View v) {
        if (etPin.getEditText().length() == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Pin tidak boleh kosong", Snackbar.LENGTH_LONG)
                    .show();
            etPin.getEditText().requestFocus();
            etPin.setError("Pin tidak boleh kosong");
        } else if (!etPin.getEditText().getText().toString().equals(etPin2.getEditText().getText().toString())) {
            Snackbar.make(findViewById(android.R.id.content), "Pin tidak sama", Snackbar.LENGTH_LONG)
                    .show();
            etPin2.getEditText().requestFocus();
            etPin2.setError("Pin tidak sama");
        } else {

            RequestParams requestParams = new RequestParams();
            requestParams.put("passport", tilemail.getEditText().getText().toString());
            requestParams.put("pin", etPin2.getEditText().getText().toString());
            requestParams.put("app_type", Constant.APP_TYPE);
            requestParams.put("imei",sIMEI);
            StringEntity entity = null;

            try {
                entity = new StringEntity(requestParams.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            LoopjHttpClient.post(this, Constant.InitPIN, entity, new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    TransitionSet seta = new TransitionSet()
                            .addTransition(new Scale(0.7f))
                            .addTransition(new Fade())
                            .setDuration(500)
                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                    TransitionManager.beginDelayedTransition(backView, seta);
                    loadingView.setVisibility(View.VISIBLE);
                    login3View.setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    int kode = 0;

                    try {
                        jresponse = response.getJSONObject("response");
                        kode = jresponse.getInt("code");
                        if (jresponse.has("access_token")) {
                            token = jresponse.getString("access_token");
                        }
                        if (jresponse.has("data")) {
                            jdata = jresponse.getJSONObject("data");
                            Log.e(TAG, "onSuccess: Data " + String.valueOf(jdata.toString()));
                            part_id = jdata.getString("part_id");
                            part_fullname = jdata.getString("part_fullname");
                            part_phone = jdata.getString("part_country_code") + jdata.getString("part_callnum");
                            if (!jdata.isNull("kuid")) {
                                session.CreateisKuid(jdata.getString("kuid"));
                            }

                            if (!jdata.isNull("rom_id")) {
                                session.CreateisRom(jdata.getString("rom_id"));
                            }

                            if (!jdata.isNull("group_id")) {
                                session.CreateisGroup(jdata.getString("group_id"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (kode == 102) {

                        new android.os.Handler().postDelayed(new Runnable() {
                            @SuppressLint("NewApi")
                            @Override
                            public void run() {
                                TransitionSet seta = new TransitionSet()
                                        .addTransition(new Scale(0.7f))
                                        .addTransition(new Fade())
                                        .setDuration(500)
                                        .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                                TransitionManager.beginDelayedTransition(backView, seta);
                                loadingView.setVisibility(View.GONE);
                                login4View.setVisibility(View.VISIBLE);
                                imgLogo.setVisibility(View.GONE);
                            }
                        }, 500);
                    } else if (kode == 100) {

                        displayFirebaseRegId();
                        SendTokenFCM(tokenfcm, token);

                    }
                    Log.d(TAG, "onSuccess: " + String.valueOf(response));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(MainActivity.this, "Terjadi kesalahan, silahkan coba lagi. . .", Toast.LENGTH_SHORT).show();
                    TransitionSet seta = new TransitionSet()
                            .addTransition(new Scale(0.7f))
                            .addTransition(new Fade())
                            .setDuration(500)
                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                    TransitionManager.beginDelayedTransition(backView, seta);
                    loadingView.setVisibility(View.GONE);
                    login3View.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e(TAG, "onFailure: " + throwable.toString());
                    Log.i(TAG, "onFailure: Muncul dong");
                    Toast.makeText(MainActivity.this, "Terjadi kesalahan, silahkan coba lagi. . .", Toast.LENGTH_SHORT).show();
                    TransitionSet seta = new TransitionSet()
                            .addTransition(new Scale(0.7f))
                            .addTransition(new Fade())
                            .setDuration(500)
                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                    TransitionManager.beginDelayedTransition(backView, seta);
                    loadingView.setVisibility(View.GONE);
                    login3View.setVisibility(View.VISIBLE);
                }


            });
        }
    }

    public void LastDo(View e) {
        Log.e(TAG, "LastDo: "+String.valueOf(!validateEmail2(tilemailp.getEditText().getText().toString())+"|"+!validateEmail(tilemailp.getEditText().getText().toString())) );
        if (type_petugas.equalsIgnoreCase("0")) {
            spinner.setError("Pilih type petugas");
        } else if (tilnama.getEditText().length() == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Input nama Anda.", Snackbar.LENGTH_LONG)
                    .show();
            tilnama.getEditText().requestFocus();
            tilnama.setError("Input nama Anda");
        } else if (tilemailp.getEditText().length() == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Input email Anda.", Snackbar.LENGTH_LONG)
                    .show();
            tilemailp.getEditText().requestFocus();
            tilemailp.setError("Input email Anda");
        } else if (!validateEmail(tilemailp.getEditText().getText().toString())) {
            Snackbar.make(findViewById(android.R.id.content), "Email Anda tidak valid.", Snackbar.LENGTH_LONG)
                    .show();
            tilemailp.getEditText().requestFocus();
            tilemailp.setError("Email Anda tidak valid");

        } else if (etPhone.length() == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Input nomor telepon Anda.", Snackbar.LENGTH_LONG)
                    .show();
            etPhone.setError("Input nomor telepon Anda");
            etPhone.requestFocus();
        } else {
            Log.e(TAG, "LastDo: " + ccode);

            // get selected radio button from radioGroup FORGENDER
            int selectedId = radioSexGroup.getCheckedRadioButtonId();
            radioSexButton = (RadioButton) findViewById(selectedId);

            // GAMBAR USER
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] ba = bytes.toByteArray();
            b64 = Base64.encodeToString(ba, Base64.NO_WRAP);

            RequestParams requestParams = new RequestParams();
            requestParams.put("passport", tilemail.getEditText().getText().toString());
            requestParams.put("part_fullname", tilnama.getEditText().getText().toString());
            requestParams.put("part_gender", radioSexButton.getText());
            requestParams.put("part_country_code", ccode);
            requestParams.put("part_callnum", etPhone.getText().toString());
            requestParams.put("part_type", type_petugas);
            requestParams.put("part_email", tilemailp.getEditText().getText().toString());
            requestParams.put("part_image", b64);
            requestParams.put("app_type", Constant.APP_TYPE);
            StringEntity entity = null;

            try {
                entity = new StringEntity(requestParams.toString());
                Log.e(TAG, "LastDo: Re " + requestParams.toString());
            } catch (UnsupportedEncodingException ea) {
                ea.printStackTrace();
            }

            LoopjHttpClient.post(this, Constant.InitProfile, entity, new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    ShowHideOne(backView, login4View, loadingView, true);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        jresponse = response.getJSONObject("response");
                        kode = jresponse.getInt("code");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    if (kode == 1) {
                        if (pins.equals("a")) {
                            pins = etPin2.getEditText().getText().toString();
                        }
                        RequestParams requestParams = new RequestParams();
                        requestParams.put("passport", tilemail.getEditText().getText().toString());
                        requestParams.put("pin", pins);
                        requestParams.put("app_type", Constant.APP_TYPE);
                        requestParams.put("imei",sIMEI);
                        StringEntity entity = null;

                        try {
                            entity = new StringEntity(requestParams.toString());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        LoopjHttpClient.post(MainActivity.this, Constant.InitPIN, entity, new JsonHttpResponseHandler() {

                            @Override
                            public void onStart() {
                                TransitionSet seta = new TransitionSet()
                                        .addTransition(new Scale(0.7f))
                                        .addTransition(new Fade())
                                        .setDuration(500)
                                        .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                                TransitionManager.beginDelayedTransition(backView, seta);
                                loadingView.setVisibility(View.VISIBLE);
                                loginView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    jresponse = response.getJSONObject("response");
                                    if (jresponse.has("access_token")) {
                                        token = jresponse.getString("access_token");
                                    }
                                    kode = jresponse.getInt("code");
                                    if (jresponse.has("data")) {
                                        jdata = jresponse.getJSONObject("data");
                                        Log.e(TAG, "onSuccess: Data " + String.valueOf(jdata.toString()));
                                        part_id = jdata.getString("part_id");
                                        part_fullname = jdata.getString("part_fullname");
                                        part_phone = jdata.getString("part_country_code") + jdata.getString("part_callnum");
                                        if (!jdata.isNull("kuid")) {
                                            session.CreateisKuid(jdata.getString("kuid"));
                                        }

                                        if (!jdata.isNull("rom_id")) {
                                            session.CreateisRom(jdata.getString("rom_id"));
                                        }

                                        if (!jdata.isNull("group_id")) {
                                            session.CreateisGroup(jdata.getString("group_id"));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (kode == 100) {

                                    displayFirebaseRegId();
                                    SendTokenFCM(tokenfcm, token);

                                } else if (kode == 501) {

                                    tilemail.getEditText().requestFocus();
                                    tilemail.setError("Passport Anda Salah");
                                    ShowHideOne(backView, loadingView, loginView, true, "Passport Anda Salah");
                                } else if (kode == -101) {
                                    tilkodepin.getEditText().requestFocus();
                                    tilkodepin.setError("Pin Anda Salah");
                                    ShowHideOne(backView, loadingView, loginView, true, "PIN Anda Salah");

                                } else if (kode == 102) {
                                    TransitionSet seta = new TransitionSet()
                                            .addTransition(new Scale(0.7f))
                                            .addTransition(new Fade())
                                            .setDuration(100)
                                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                                    TransitionManager.beginDelayedTransition(backView, seta);
                                    login3View.setVisibility(View.GONE);
                                    loadingView.setVisibility(View.VISIBLE);

                                    new android.os.Handler().postDelayed(new Runnable() {
                                        @SuppressLint("NewApi")
                                        @Override
                                        public void run() {
                                            TransitionSet seta = new TransitionSet()
                                                    .addTransition(new Scale(0.7f))
                                                    .addTransition(new Fade())
                                                    .setDuration(500)
                                                    .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                                            TransitionManager.beginDelayedTransition(backView, seta);
                                            loadingView.setVisibility(View.GONE);
                                            login4View.setVisibility(View.VISIBLE);
                                            imgLogo.setVisibility(View.GONE);
                                        }
                                    }, 500);
                                    step = 2;
                                }
                                Log.d(TAG, "onSuccess: " + String.valueOf(response));
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                            }
                        });
                    }

                    Log.d(TAG, "onSuccess: " + String.valueOf(response));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(MainActivity.this, "Terjadi kesalahan, silahkan coba lagi. . .", Toast.LENGTH_SHORT).show();
                    TransitionSet seta = new TransitionSet()
                            .addTransition(new Scale(0.7f))
                            .addTransition(new Fade())
                            .setDuration(500)
                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                    TransitionManager.beginDelayedTransition(backView, seta);
                    loadingView.setVisibility(View.GONE);
                    login4View.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e(TAG, "onFailure: " + throwable.toString());
                    Log.i(TAG, "onFailure: Muncul dong");
                    Toast.makeText(MainActivity.this, "Terjadi kesalahan, silahkan coba lagi. . .", Toast.LENGTH_SHORT).show();
                    TransitionSet seta = new TransitionSet()
                            .addTransition(new Scale(0.7f))
                            .addTransition(new Fade())
                            .setDuration(500)
                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                    TransitionManager.beginDelayedTransition(backView, seta);
                    loadingView.setVisibility(View.GONE);
                    login4View.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void SendTokenFCM(String tokens, String acces) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("fcm_code", tokens);
        requestParams.put("app_type", Constant.APP_TYPE);
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
                            session.createLoginSession(tilemail.getEditText().getText().toString(), part_fullname, part_id, part_phone, token, pins, type_petugas);
                            // Start TRACK
                            startAlarmManager();
                            Intent i = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }, 500);
                }
                Log.d(TAG, "onSuccess: " + String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(MainActivity.this, "Terjadi kesalahan, silahkan coba lagi. . .", Toast.LENGTH_SHORT).show();
                TransitionSet seta = new TransitionSet()
                        .addTransition(new Scale(0.7f))
                        .addTransition(new Fade())
                        .setDuration(500)
                        .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                TransitionManager.beginDelayedTransition(backView, seta);
                loadingView.setVisibility(View.GONE);
                login4View.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "onFailure: " + throwable.toString());
                Log.i(TAG, "onFailure: Muncul dong");
                Toast.makeText(MainActivity.this, "Terjadi kesalahan, silahkan coba lagi. . .", Toast.LENGTH_SHORT).show();
                TransitionSet seta = new TransitionSet()
                        .addTransition(new Scale(0.7f))
                        .addTransition(new Fade())
                        .setDuration(500)
                        .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                TransitionManager.beginDelayedTransition(backView, seta);
                loadingView.setVisibility(View.GONE);
                login4View.setVisibility(View.VISIBLE);
            }
        });
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

    public void ShowHideOne(final ViewGroup main, final View v1, final View v2, final boolean visible, final String mes) {
        new android.os.Handler().postDelayed(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                TransitionSet seta = new TransitionSet()
                        .addTransition(new Scale(0.7f))
                        .addTransition(new Fade())
                        .setDuration(500)
                        .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                TransitionManager.beginDelayedTransition(main, seta);
                v1.setVisibility(View.GONE);
                v2.setVisibility(View.VISIBLE);
                Snackbar.make(findViewById(android.R.id.content), mes, Snackbar.LENGTH_LONG)
                        .show();
            }
        }, 500);
    }

    public void ShowHideOne(final ViewGroup main, final View v1, final View v2, final boolean visible) {

        TransitionSet seta = new TransitionSet()
                .addTransition(new Scale(0.7f))
                .addTransition(new Fade())
                .setDuration(500)
                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
        TransitionManager.beginDelayedTransition(main, seta);
        v1.setVisibility(View.GONE);
        v2.setVisibility(View.VISIBLE);
    }


    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {

            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);

                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
//                    myBitmap = rotateImageIfRequired(myBitmap, picUri);
                    myBitmap = getResizedBitmap(myBitmap, 500);

                    imgAkun.setImageBitmap(myBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {


                bitmap = (Bitmap) data.getExtras().get("data");

                myBitmap = bitmap;
                imgAkun.setImageBitmap(myBitmap);

            }

        }

    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {
                        Log.e(TAG, "onRequestPermissionsResult: Has "+perms );
                        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                        session.CreateIMEI(telephonyManager.getDeviceId());
                        Log.w(TAG, "onCreate: IMEI : "+telephonyManager.getDeviceId());
                    } else {

                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());

                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

}
