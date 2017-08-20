package com.huji.apps.haji.petugas;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huji.apps.haji.petugas.Utils.Config;
import com.huji.apps.haji.petugas.Utils.Constant;
import com.huji.apps.haji.petugas.Utils.LoopjHttpClient;
import com.huji.apps.haji.petugas.Utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ShowHelpActivity extends AppCompatActivity {

    CountDownTimer cdt;

    private static final String TAG = "ShowHelpActivity";

    public BroadcastReceiver mRegistrationBroadcastReceiver;
    SessionManager session;
    JSONObject jResponse = new JSONObject(), jData = new JSONObject();
    JSONArray jAJemaah = new JSONArray(), jAPetugas = new JSONArray(), jAData = new JSONArray();
    String jumID, sLongitude, sLatitude, sTokens, sIMEI;
    TextView mTextField;
    View vHelpSearch,lHelp;
    int backs = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_help);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Help");
        mTextField = (TextView) findViewById(R.id.tvcounter);

        lHelp = findViewById(R.id.lHelp);
        vHelpSearch = findViewById(R.id.HelpSearchView);


        // Session class instance
        session = new SessionManager(getApplicationContext());
        sTokens = session.getToken();
        sIMEI = session.getImei();

        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    if (cdt != null) {
                        cdt.cancel();
                    }
                    Log.e(TAG, "onReceive: PUSH");
//                    TransitionSet seta = new TransitionSet()
//                            .addTransition(new Scale(0.7f))
//                            .addTransition(new Fade())
//                            .setDuration(500)
//                            .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
//                    TransitionManager.beginDelayedTransition(backview, seta);
//                    vHelpSearch.setVisibility(View.GONE);
//                    vHelpGet.setVisibility(View.VISIBLE);
//
//                    Picasso.with(ShowHelpActivity.this)
//                            .load(intent.getStringExtra("image"))
//                            .placeholder(R.drawable.profilee)
//                            .into(imgHPetugas);
                    Log.w(TAG, "onReceive: " + intent.getStringExtra("name"));
                    Log.w(TAG, "onReceive: " + intent.getStringExtra("image"));
                    Log.w(TAG, "onReceive: " + intent.getStringExtra("lat"));
                    Log.w(TAG, "onReceive: " + intent.getStringExtra("lon"));
                    Log.w(TAG, "onReceive: " + intent.getStringExtra("tel"));

                    Intent intent1 = new Intent(ShowHelpActivity.this, HelpPetugasActivity.class);
                    intent1.putExtra("name", intent.getStringExtra("name"));
                    intent1.putExtra("image", intent.getStringExtra("image"));
                    intent1.putExtra("latitude", intent.getStringExtra("lat"));
                    intent1.putExtra("longitude", intent.getStringExtra("lon"));
                    intent1.putExtra("sphone", intent.getStringExtra("tel"));
                    intent1.putExtra("id", intent.getStringExtra("id"));
                    startActivity(intent1);
                    finish();
                }
            }
        };
    }
    @Override
    public void onResume() {
        super.onResume();
        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CancelHelp();
        Log.e(TAG, "onBackPressed: Waw" );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void Help() {

        sLatitude = String.valueOf(session.getLat());
        sLongitude = String.valueOf(session.getLon());
        session.createLatLon(sLatitude, sLongitude);
        LoopjHttpClient.get(Constant.HelpMe + sTokens + "?latitude=" + sLatitude + "&longitude=" + sLongitude+"&imei="+sIMEI, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, "onSuccess: " + String.valueOf(response));
                try {
                    jResponse = response.getJSONObject("response");

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

    public void HelpClick(View v) {
//        if (mAddressOutput.equalsIgnoreCase(Constant.POSITION) || mAddressOutput.equals("Saudi Arabia")) {
//            toolbar.setVisibility(View.GONE);
            vHelpSearch.setVisibility(View.VISIBLE);
            backs = 99;
            Help();
            RunCount();
//        } else {
//            Toast.makeText(this, "Fitur belum dapat digunakan dilokasi ini.", Toast.LENGTH_SHORT).show();
//        }
    }

    public void CancelHelp(View v) {
//        toolbar.setVisibility(View.VISIBLE);
        vHelpSearch.setVisibility(View.GONE);
        backs = 0;
        CancelHelp();
    }

    public void RunCount() {
        cdt = new CountDownTimer(60000, 1000) {

            public void onTick(long timeDiff) {

                int seconds = (int) (timeDiff / 1000) % 60;
                int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                if (seconds > 9)
                    mTextField.setText("0" + minutes + ":" + seconds);
                else
                    mTextField.setText("0" + minutes + ":0" + seconds);

                Log.e(TAG, "onTick: menit " + minutes + " | " + seconds );
            }

            public void onFinish() {
                mTextField.setText("done!");
                vHelpSearch.setVisibility(View.GONE);
                backs = 0;
                CancelHelp();
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowHelpActivity.this);
                builder.setTitle("Informasi");
                builder.setMessage("Petugas sedang sibuk.\nSilahkan coba lagi.");
                builder.setCancelable(false);
                builder.setNegativeButton("OK", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                // TODO Auto-generated method stub
                                dialog.cancel();
                            }
                        }).show();
            }
        }.start();
    }


    public void CancelHelp() {
        if (cdt != null)
            cdt.cancel();

        Log.e(TAG, "CancelHelp: Click" );
        LoopjHttpClient.get(Constant.CancelHelp + sTokens+"?imei="+sIMEI, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, "onSuccess: " + String.valueOf(response));
                try {
                    jResponse = response.getJSONObject("response");

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
