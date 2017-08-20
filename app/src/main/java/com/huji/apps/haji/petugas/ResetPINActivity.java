package com.huji.apps.haji.petugas;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.huji.apps.haji.petugas.Adapters.CustomListViewLJResetAdapter;
import com.huji.apps.haji.petugas.Beans.RowItemLJReset;
import com.huji.apps.haji.petugas.Utils.Constant;
import com.huji.apps.haji.petugas.Utils.LoopjHttpClient;
import com.huji.apps.haji.petugas.Utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ResetPINActivity extends AppCompatActivity implements CustomListViewLJResetAdapter.customButtonListener {

    private static final String TAG = "ResetPINActivity";
    String sTokens,sType, sFilter;
    String[] sNama = new String[0], sImg, sTel, sLJLat, slJLon, sLJTime, sLatJ, sLonJ, sLatP, sLonP,
            sImageBC, sImgFull, sNamaBC, sTimeBC, sJudulBC, sIsiBC, sImgPBC;

    Boolean[] sStatus;

    JSONObject jResponse = new JSONObject(), jData = new JSONObject();

    JSONArray jAJemaah = new JSONArray(), jAPetugas = new JSONArray();

    List<RowItemLJReset> rowItemLjs = new ArrayList<>();
    ListView lvLj;
    View vLjNo;
    SessionManager sessionManager;
    Boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Reset PIN");
        lvLj = (ListView) findViewById(R.id.lvLj);
        vLjNo = findViewById(R.id.lListEmpty);
        sessionManager = new SessionManager(this);
        sTokens = sessionManager.getToken();
        sType = sessionManager.getType();

        IsiFilter();
        GetListJemaah();
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    public void GetListJemaah() {
        LoopjHttpClient.get(Constant.ListJemaah + sTokens + "?filter_by=" + sFilter + "&filter_kbih=yes", new JsonHttpResponseHandler() {
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
                            for (int i = 0; i < jAJemaah.length(); i++) {
                                jData = jAJemaah.getJSONObject(i);

                                sImg[i] = jData.getString("part_image");
                                sNama[i] = jData.getString("part_fullname");
                                sStatus[i] = jData.getBoolean("active");
                                sTel[i] = jData.getString("part_id");


                            }
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
        } else {
            for (int i = 0; i < sNama.length; i++) {
                rowItemLjs.add(new RowItemLJReset(sImg[i], sNama[i], sTel[i]));
            }
            vLjNo.setVisibility(View.GONE);
            lvLj.setVisibility(View.VISIBLE);
            CustomListViewLJResetAdapter adapter = new CustomListViewLJResetAdapter(this, R.layout.itemjemaahreset, rowItemLjs);
            adapter.notifyDataSetChanged();
            adapter.setCustomButtonListner(this);
            lvLj.setAdapter(adapter);
            lvLj.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    onButtonClickListener(i, sTel[i]);

                }
            });
        }
    }

    @Override
    public void onButtonClickListener(int position, final String value) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Apakah Anda yakin ingin mengereset PIN Jemaah " + sNama[position] + "ini?");
        builder.setCancelable(false);
        builder.setPositiveButton("YA", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DoReset(value);
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

    @Override
    public void onButtonClickUnListener(int position, String value) {

    }

    void DoReset(String value) {

        Log.w(TAG, "onButtonClickListener: " + value);
        RequestParams params = new RequestParams();
        params.put("part_id", value);
//
        StringEntity entity = null;

        try {
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(this, Constant.ResetPIN + sTokens, entity, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, "onSuccess: ResetPIN" + String.valueOf(response));
                try {
                    jResponse = response.getJSONObject(Constant.RESPONSE);
                    status = jResponse.getBoolean(Constant.STATUS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status) {
                    Toast.makeText(ResetPINActivity.this, "Berhasil mereset", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ResetPINActivity.this, "Coba lagi.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: ResetPIN" + String.valueOf(responseString));
                Toast.makeText(ResetPINActivity.this, "Coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
