package com.huji.apps.haji.petugas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.huji.apps.haji.petugas.Utils.Constant;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import fr.ganfra.materialspinner.MaterialSpinner;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class ProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "ProfileActivity";
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private static final SimpleDateFormat FORMATTERSAVE = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat FORMATTERINDO = new SimpleDateFormat("dd MMMM yyyy");
    String[] kuids, kloter_id, embarkasi_id, rom_ids, group_ids;
    String kuid = "0", rom_id = "0", group_id = "0", sTokens, sEmber;
    JSONObject jResponse = new JSONObject(), jData = new JSONObject();
    JSONArray jAJemaah = new JSONArray(), jAPetugas = new JSONArray(), jAData = new JSONArray();
    SessionManager sessionManager;
    JSONObject jresponse = new JSONObject(), jdata = new JSONObject();
    TextInputLayout tilNama, tilEmail, tilTglLahir, tilTmptLahir, tilPINLama, tilPINBaru, tilPINBaru2;
    EditText etTel;
    ProgressDialog progressDialog;
    ImageView imgLogo, imgAkun;
    CountryCodePicker ccp;
    TextView tvType, tvNama, tvTelp, tvJK, tvEmail, tvTglLahir, tvTglLahirEdit, tvKuid, tvRom, tvGroup;

    View llEdit, llShow, llGanti, lEkuid, lRom, lGroup;
    ViewGroup backView;
    int stateklik = 0, updateiimage = 0, pos_type = -1;
    boolean visible = true, status;
    String sNama, sTelpCode, sTelp, sJK, sEmail, sTgl, sImgFull, sImg32, sKuid, sRom_id, sGroup_id, sEmbid, sKloterid;

    String param = "[\"part_fullname\", \"part_type\", \"part_country_code\",\"part_callnum\",\"part_birthdate\",\"part_gender\", \"part_email\"]", valus;
    JSONObject jsonObject;
    String b64, type_petugas = "0";
    Bitmap myBitmap;
    Uri picUri;
    Uri mCropImageUri;
    DatePickerDialog dpd;
    Calendar now;
    String[] ITEMS = {"ketua kloter", "bimbingan ibadah", "perawat", "dokter", "ketua rombongan", "ketua regu"};
    MaterialSpinner spinner;
    Button btnEditP;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();

    public static String FormatDOB(String a) {
        Date date = null;
        SimpleDateFormat sdftimehour = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdfdate.parse(a);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String kirim = sdftimehour.format(date);
        Log.e("FDOB", kirim);
        return kirim;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Profile");

        tilNama = (TextInputLayout) findViewById(R.id.nameWrapper);
        etTel = (EditText) findViewById(R.id.editText_phones);
        tilEmail = (TextInputLayout) findViewById(R.id.emailWrapper);
        tilTglLahir = (TextInputLayout) findViewById(R.id.tglLahirWrapper);
        imgAkun = (ImageView) findViewById(R.id.icons);
        imgLogo = (ImageView) findViewById(R.id.iconscam);

        tilPINLama = (TextInputLayout) findViewById(R.id.etPinlama);
        tilPINBaru = (TextInputLayout) findViewById(R.id.etPin1);
        tilPINBaru2 = (TextInputLayout) findViewById(R.id.etPin2);

        radioSexGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.setCountryForPhoneCode(+93);

        tvKuid = (TextView) findViewById(R.id.tvkuide);
        tvRom = (TextView) findViewById(R.id.tvrome);
        tvGroup = (TextView) findViewById(R.id.tvgroupe);

        tvType = (TextView) findViewById(R.id.tvTypeP);
        tvNama = (TextView) findViewById(R.id.tvNamaP);
        tvTelp = (TextView) findViewById(R.id.tvTelpP);
        tvJK = (TextView) findViewById(R.id.tvJKP);
        tvEmail = (TextView) findViewById(R.id.tvEmailP);
        tvTglLahir = (TextView) findViewById(R.id.tvTglLahirP);
        tvTglLahirEdit = (TextView) findViewById(R.id.tvpdobsave);

        backView = (ViewGroup) findViewById(R.id.main_profile);
        llEdit = findViewById(R.id.llEditProfile);
        llShow = findViewById(R.id.llShowProfile);
        llGanti = findViewById(R.id.lGantiEKEdit);

        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
        sEmail = sessionManager.getIdsuper();

        now = Calendar.getInstance();

        // Datepickerdialog
        dpd = DatePickerDialog.newInstance(
                (DatePickerDialog.OnDateSetListener) ProfileActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        CloseError(tilPINLama);
        CloseError(tilPINBaru);
        CloseError(tilPINBaru2);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (MaterialSpinner) findViewById(R.id.spintypes);
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

        lEkuid = findViewById(R.id.PilihanKuide);
        lEkuid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowEmbarkasi();
            }
        });

        lRom = findViewById(R.id.PilihanRombongane);
        lRom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowRombongan();
            }
        });

        lGroup = findViewById(R.id.PilihanGroupe);
        lGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowGroup();
            }
        });

        btnEditP = (Button) findViewById(R.id.buttonProf);
        btnEditP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnEditP.getText().toString().equalsIgnoreCase("Edit")) {
                    toEdit();
                } else {
                    toSave();
                }
            }
        });

        sTokens = sessionManager.getToken();

        try {

            jAData = new JSONArray(sessionManager.isKuid_DATA());

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

        if (sessionManager.isKuid_DATA().equalsIgnoreCase("0")) {
            getListEmbarkasi();
        }
        if (sessionManager.isRom_DATA().equalsIgnoreCase("0")) {
            getListRombongan();
        }
        if (sessionManager.isGroup_DATA().equalsIgnoreCase("0")) {
            getListGroup();
        }

        GetData();
    }

    public void getListEmbarkasi() {
        Log.e(TAG, "getListEmbarkasi: " + Constant.GetKloter);
        LoopjHttpClient.get(Constant.GetKloter, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e(TAG, "onSuccess: Kloter" + String.valueOf(response));
                sessionManager.CreateisKuid_DATA(String.valueOf(response));

//                if (sessionManager.isKuid().equalsIgnoreCase("0")) {
//                    ShowEmbarkasi();
//                }
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
                sessionManager.CreateisRom_DATA(String.valueOf(response));

//                if (sessionManager.isRom().equalsIgnoreCase("0")) {
//                    ShowRombongan();
//                }
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
                sessionManager.CreateisGroup_DATA(String.valueOf(response));

//                if (sessionManager.isGroup().equalsIgnoreCase("0")) {
//                    ShowGroup();
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: " + String.valueOf(responseString));
            }
        });
    }

    public void ShowEmbarkasi() {
        try {

            jAData = new JSONArray(sessionManager.isKuid_DATA());

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
        selectedItems.setText(tvKuid.getText().toString());

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
                sEmber = item;
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
                            Toast.makeText(ProfileActivity.this, "Anda belum memilih Embarkasi", Toast.LENGTH_LONG).show();
                            ShowEmbarkasi();
                        } else {
                            SaveEmbarkasi(kuid);
                        }
                    }
                });
        dialogBuilder.setNegativeButton("Batal", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                    }
                });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

    public void ShowRombongan() {
        try {

            jAData = new JSONArray(sessionManager.isRom_DATA());

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
        selectedItems.setText(sRom_id);

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
                            Toast.makeText(ProfileActivity.this, "Anda belum memilih Rombongan", Toast.LENGTH_LONG).show();
                            ShowRombongan();
                        } else {
                            SaveRombongan(rom_id);
                        }
                    }
                });
        dialogBuilder.setNegativeButton("Batal", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                    }
                });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

    public void ShowGroup() {
        try {

            jAData = new JSONArray(sessionManager.isGroup_DATA());

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
        selectedItems.setText(sGroup_id);

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
                            Toast.makeText(ProfileActivity.this, "Anda belum memilih Group", Toast.LENGTH_LONG).show();
                            ShowGroup();
                        } else {
                            SaveGroup(group_id);
                        }
                    }
                });
        dialogBuilder.setNegativeButton("Batal", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
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
                progressDialog.setMessage("Menyimpan. . .");
                progressDialog.show();

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
                    tvKuid.setText(sEmber);
                    sessionManager.CreateisKuid(a);

                    Toast.makeText(ProfileActivity.this, "Data tersimpan", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onSuccess: " + String.valueOf(response));
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "onFailure: " + responseString);
                Log.i(TAG, "onFailure: Muncul dong");
                Toast.makeText(ProfileActivity.this, "Koneksi bermasalah, silahkan coba lagi.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "onFailure: " + throwable.toString());
                Log.i(TAG, "onFailure: Muncul dong");
                Toast.makeText(ProfileActivity.this, "Koneksi bermasalah, silahkan coba lagi.", Toast.LENGTH_SHORT).show();
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
                progressDialog.setMessage("Menyimpan. . .");
                progressDialog.show();
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
                    tvRom.setText(rom_id);
                    sessionManager.CreateisRom(a);
                    Toast.makeText(ProfileActivity.this, "Data tersimpan", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onSuccess: " + String.valueOf(response));
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "onFailure: " + responseString);
                Log.i(TAG, "onFailure: Muncul dong");
                Toast.makeText(ProfileActivity.this, "Koneksi bermasalah, silahkan coba lagi.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "onFailure: " + throwable.toString());
                Log.i(TAG, "onFailure: Muncul dong");
                Toast.makeText(ProfileActivity.this, "Koneksi bermasalah, silahkan coba lagi.", Toast.LENGTH_SHORT).show();
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
                progressDialog.setMessage("Menyimpan. . .");
                progressDialog.show();
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
                    tvGroup.setText(group_id);
                    sessionManager.CreateisGroup(a);
                    Toast.makeText(ProfileActivity.this, "Data tersimpan", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onSuccess: " + String.valueOf(response));
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "onFailure: " + responseString);
                Log.i(TAG, "onFailure: Muncul dong");
                Toast.makeText(ProfileActivity.this, "Koneksi bermasalah, silahkan coba lagi.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "onFailure: " + throwable.toString());
                Log.i(TAG, "onFailure: Muncul dong");
                Toast.makeText(ProfileActivity.this, "Koneksi bermasalah, silahkan coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.mains, menu);
//        if (stateklik == 1) {
//            menu.findItem(R.id.action_edit).setVisible(false);
//            menu.findItem(R.id.action_save).setVisible(true);
//        } else if (stateklik == 0) {
//            menu.findItem(R.id.action_edit).setVisible(true);
//            menu.findItem(R.id.action_save).setVisible(false);
//        } else {
//            menu.findItem(R.id.action_edit).setVisible(false);
//            menu.findItem(R.id.action_save).setVisible(false);
//        }
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_edit) {
            toEdit();
            invalidateOptionsMenu();
        } else if (id == R.id.action_save) {
            toSave();
        }

        return super.onOptionsItemSelected(item);
    }

    public void ToGantiPIN(View v) {
        TransitionSet seta = new TransitionSet()
                .addTransition(new Scale(0.7f))
                .addTransition(new Fade())
                .setDuration(500)
                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
        TransitionManager.beginDelayedTransition(backView, seta);
        stateklik = 2;
        invalidateOptionsMenu();
    }

    public void BatalPin(View v) {
        TransitionSet seta = new TransitionSet()
                .addTransition(new Scale(0.7f))
                .addTransition(new Fade())
                .setDuration(500)
                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
        TransitionManager.beginDelayedTransition(backView, seta);
        stateklik = 0;
        invalidateOptionsMenu();
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
                imgAkun.setImageURI(result.getUri());
                updateiimage = 1;
//                Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
//            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {

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

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(false)
                .setRequestedSize(512, 512)
                .setAspectRatio(1, 1)
                .start(this);
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void toEdit() {
        TransitionSet seta = new TransitionSet()
                .addTransition(new Scale(0.7f))
                .addTransition(new Fade())
                .setDuration(500)
                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
        TransitionManager.beginDelayedTransition(backView, seta);
        llShow.setVisibility(View.GONE);
        llEdit.setVisibility(View.VISIBLE);
        llGanti.setVisibility(View.GONE);
        imgLogo.setVisibility(View.VISIBLE);
        stateklik = 1;
        tilNama.getEditText().setText(sNama);
        ccp.setCountryForPhoneCode(Integer.valueOf(sTelpCode.replaceAll("\\+", "")));
        etTel.setText(sTelp);
        if (sJK.equalsIgnoreCase("Laki-laki")) {
            radioSexGroup.check(R.id.radioButton1);
        } else {
            radioSexGroup.check(R.id.radioButton2);
        }
        tilEmail.getEditText().setText(sEmail);
        tilTglLahir.getEditText().setText(sTgl);
        tvTglLahirEdit.setText(FormatDOB(sTgl));
        btnEditP.setText("Simpan");

    }

    public void toSave() {

        if (type_petugas.equalsIgnoreCase("0")) {
            spinner.setError("Pilih type petugas");
        } else {
            // get selected radio button from radioGroup FORGENDER
            int selectedId = radioSexGroup.getCheckedRadioButtonId();
            radioSexButton = (RadioButton) findViewById(selectedId);

            valus = "[\"" + tilNama.getEditText().getText().toString() + "\",\"" +
                    type_petugas + "\",\"" +
                    ccp.getSelectedCountryCodeWithPlus() + "\",\"" +
                    etTel.getText().toString() + "\",\"" +
                    tilTglLahir.getEditText().getText().toString() + "\",\"" +
                    radioSexButton.getText().toString() + "\",\"" +
                    tilEmail.getEditText().getText().toString() + "\"]";
            Log.e(TAG, "toSave: " + ccp.getSelectedCountryCodeWithPlus() + "\n" + valus);
            UpdateBasic();
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void UpdateBasic() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("field_param", param);
        requestParams.put("value_param", valus);
        StringEntity entitys = null;
        ByteArrayEntity entity = null;
        String semua = "{\"field_param\":" + param + ",\"value_param\":" + valus + "}";
        try {
            jsonObject = new JSONObject(semua);
            Log.w(TAG, "UpdateBasic: " + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            entitys = new StringEntity(requestParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(this, Constant.UpdateProfile + sessionManager.getToken(), entity, "application/json", new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                progressDialog.setMessage("Menyimpan. . .");
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int kode = 0;

                String jum_id, jum_fullname, token = "a";

                try {
                    jresponse = response.getJSONObject("response");
                    if (jresponse.has("access_token")) {
                        token = jresponse.getString("access_token");
                    }
                    status = jresponse.getBoolean("status");
                    if (status) {
                        if (updateiimage == 0) {
                            TransitionSet seta = new TransitionSet()
                                    .addTransition(new Scale(0.7f))
                                    .addTransition(new Fade())
                                    .setDuration(500)
                                    .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                            TransitionManager.beginDelayedTransition(backView, seta);
                            llEdit.setVisibility(View.GONE);
                            llShow.setVisibility(View.VISIBLE);
                            imgLogo.setVisibility(View.GONE);
                            llGanti.setVisibility(View.VISIBLE);
                            stateklik = 0;
                            invalidateOptionsMenu();
                            GetData();
                        } else {
                            UpdatePicture();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Gagal, coba lagi", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onSuccess: Upbasic" + String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "onFailure: Upbasic" + responseString);
            }
        });
    }

    public void UpdatePicture() {
        BitmapDrawable drawable = (BitmapDrawable) imgAkun.getDrawable();
        myBitmap = drawable.getBitmap();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] ba = bytes.toByteArray();
        b64 = Base64.encodeToString(ba, Base64.NO_WRAP);

        RequestParams params = new RequestParams();
        params.put("picture_base64", b64);
//
        StringEntity entity = null;

        try {
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LoopjHttpClient.post(this, Constant.UpdatePicture + sessionManager.getToken(), entity, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                progressDialog.setMessage("Loading. . .");
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {


                try {
                    jresponse = response.getJSONObject("response");
                    status = jresponse.getBoolean("status");
                    if (status) {
                        TransitionSet seta = new TransitionSet()
                                .addTransition(new Scale(0.7f))
                                .addTransition(new Fade())
                                .setDuration(500)
                                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
                        TransitionManager.beginDelayedTransition(backView, seta);
                        llEdit.setVisibility(View.GONE);
                        llShow.setVisibility(View.VISIBLE);
                        imgLogo.setVisibility(View.GONE);
                        stateklik = 0;
                        invalidateOptionsMenu();
                        GetData();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Gagal, coba lagi", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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

    public void GetData() {
        btnEditP.setText("Edit");
        Log.e(TAG, "GetData: " + sEmail);
        RequestParams requestParams = new RequestParams();
        requestParams.put("passport", sessionManager.getPassport());
        requestParams.put("pin", sessionManager.getPin());
        requestParams.put("app_type", Constant.APP_TYPE);
        requestParams.put("imei", sessionManager.getImei());
        StringEntity entity = null;

        try {
            entity = new StringEntity(requestParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(this, Constant.InitPIN, entity, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                progressDialog.setMessage("Loading. . .");
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int kode = 0;

                String jum_id, jum_fullname, token = "a";

                try {
                    jresponse = response.getJSONObject("response");
                    if (jresponse.has("access_token")) {
                        token = jresponse.getString("access_token");
                    }
                    kode = jresponse.getInt("code");
                    if (jresponse.has("data")) {
                        jdata = jresponse.getJSONObject("data");
                        Log.e(TAG, "onSuccess: Data " + String.valueOf(jdata.toString()));

                        sNama = jdata.getString("part_fullname");
                        sTelpCode = jdata.getString("part_country_code");
                        sTelp = jdata.getString("part_callnum");
                        sJK = jdata.getString("part_gender");
                        sEmail = jdata.getString("part_email");
                        sTgl = jdata.getString("part_birthdate");
                        sImg32 = jdata.getString("part_image_200");
                        sImgFull = jdata.getString("part_image");
                        type_petugas = jdata.getString("part_type");

                        for (int i = 0; i < ITEMS.length; i++) {
                            if (ITEMS[i].equalsIgnoreCase(type_petugas)) {
                                pos_type = i + 1;
                                Log.e(TAG, "toEdit: " + ITEMS[i]);
                                break;
                            }
                        }

                        if (jdata.isNull("kuid")) {
                            sKuid = "-";
                            sEmbid = "-";
                            sKloterid = "";
                        } else {
                            sKuid = jdata.getString("kuid");
                            sEmbid = jdata.getString("embarkasi_id");
                            sKloterid = jdata.getString("kloter_id");
                        }

                        if (jdata.isNull("rom_id"))
                            sRom_id = "-";
                        else
                            sRom_id = jdata.getString("rom_id");

                        if (jdata.isNull("group_id"))
                            sGroup_id = "-";
                        else
                            sGroup_id = jdata.getString("group_id");

                        tvKuid.setText(sEmbid + " " + sKloterid);
                        tvRom.setText(sRom_id);
                        tvGroup.setText(sGroup_id);

                        String[] ITEMS = {"ketua kloter", "bimbingan ibadah", "perawat", "dokter", "ketua rombongan", "ketua regu"};

                        if (type_petugas.equalsIgnoreCase("ketua kloter") || type_petugas.equalsIgnoreCase("bimbingan ibadah") ||
                                type_petugas.equalsIgnoreCase("perawat") || type_petugas.equalsIgnoreCase("dokter")) {
                            lEkuid.setVisibility(View.VISIBLE);
                            lRom.setVisibility(View.GONE);
                            lGroup.setVisibility(View.GONE);
                        } else if (type_petugas.equalsIgnoreCase("ketua rombongan")) {
                            lEkuid.setVisibility(View.VISIBLE);
                            lRom.setVisibility(View.VISIBLE);
                            lGroup.setVisibility(View.GONE);
                        } else if (type_petugas.equalsIgnoreCase("ketua regu")){
                            lEkuid.setVisibility(View.VISIBLE);
                            lRom.setVisibility(View.VISIBLE);
                            lGroup.setVisibility(View.VISIBLE);
                        }

                        tvType.setText(type_petugas);
                        tvNama.setText(sNama);
                        tvTelp.setText(sTelpCode + sTelp);
                        tvJK.setText(sJK);
                        tvEmail.setText(sEmail);
                        tvTglLahir.setText(FormatDOB(sTgl));
                        Picasso.with(ProfileActivity.this)
                                .load(sImg32)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .placeholder(R.drawable.profilee)
                                .into(imgAkun, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                        progressDialog.dismiss();
                                    }

                                    @Override
                                    public void onError() {

                                        progressDialog.dismiss();
                                    }
                                });

                        spinner.setSelection(pos_type, false);
                        sessionManager.createType(type_petugas);
                    }
                    if (kode == 104){
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProfileActivity.this);
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
                } catch (JSONException e) {
                    e.printStackTrace();
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

    public void ShowDOBEdit(View view) {
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        now.set(year, monthOfYear, dayOfMonth);
        tvTglLahirEdit.setText(FORMATTERINDO.format(now.getTime()));
        tilTglLahir.getEditText().setText(FORMATTERSAVE.format(now.getTime()));
        Log.w(TAG, "onDateSet: " + String.valueOf(FORMATTERSAVE.format(now.getTime())));
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
}
