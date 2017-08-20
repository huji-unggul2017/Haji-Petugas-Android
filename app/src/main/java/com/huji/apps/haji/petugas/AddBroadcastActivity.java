package com.huji.apps.haji.petugas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.huji.apps.haji.petugas.Utils.Constant;
import com.huji.apps.haji.petugas.Utils.LoopjHttpClient;
import com.huji.apps.haji.petugas.Utils.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class AddBroadcastActivity extends AppCompatActivity {

    private static final String TAG = "Simple";
    String b64 = "", sToken, typenotif = "", sUTC, sKirimke = "jemaah";

    int imgklik = 0;

    ImageView imgsbrod, imgsend;

    EditText etJudul, etIsi;

    Uri mCropImageUri;

    Bitmap myBitmap;

    SessionManager session;

    JSONObject jResponse = new JSONObject();

    Boolean status;

    View loadView, rlView, rlAwal;

    boolean visible = true;

    ViewGroup backview;

    RadioGroup radioGroup1;

    int back = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_broadcast);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etJudul = (EditText) findViewById(R.id.etJudul);
        etIsi = (EditText) findViewById(R.id.etIsi);

        imgsend = (ImageView) findViewById(R.id.imgsend);
        imgsbrod = (ImageView) findViewById(R.id.imgsbrod);
        imgsbrod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectImageClick(view);
            }
        });

        backview = (ViewGroup) findViewById(R.id.activity_broadcast);

        // Session class instance
        session = new SessionManager(getApplicationContext());
        sToken = session.getToken();

        loadView = findViewById(R.id.loadingView);
        rlView = findViewById(R.id.rlView);
        rlAwal = findViewById(R.id.rlAwalBC);

        radioGroup1 = (RadioGroup) findViewById(R.id.lift);
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.r91) {
                    //do work when radioButton1 is active
                    sKirimke = "jemaah";

                } else  if (checkedId == R.id.r92) {
                    //do work when radioButton2 is active
                    sKirimke = "petugas";

                } else  if (checkedId == R.id.r93) {
                    //do work when radioButton3 is active
                    sKirimke = "all";
                }

            }
        });

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");
        sUTC = date.format(currentLocalTime);
        sUTC = sUTC.replaceAll("\\+","pls");
        sUTC = sUTC.replaceAll("\\-","mns");
        Log.e(TAG, "onCreate: UTC " + sUTC );
    }

    public void Show(View v1, View v2){
        TransitionSet seta = new TransitionSet()
                .addTransition(new Scale(0.7f))
                .addTransition(new Fade())
                .setDuration(500)
                .setInterpolator(visible ? new LinearOutSlowInInterpolator() : new FastOutLinearInInterpolator());
        TransitionManager.beginDelayedTransition(backview, seta);
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        clearInputan();
        if (back == 1){
            Show(rlAwal, rlView);
            back = 0;
            setTitle("Kirim Broadcast");
        } else
            super.onBackPressed();
    }

    public void DoAddNormal(View v) {
        typenotif = "information";
        Show(rlView,rlAwal);
        back = 1;
        setTitle("Kirim Broadcast Normal");
    }

    public void DoAddEmergency(View v){
        typenotif = "emergency";
        Show(rlView,rlAwal);
        back = 1;
        setTitle("Kirim Broadcast Darurat");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
//        if (id == R.id.send_broadcast) {
//            SendBroadcast();
//        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate( R.menu.done_menu, menu );
//
//        return true;
//    }

    public void clearInputan() {
        etJudul.setText("");
        etIsi.setText("");
        imgsend.setImageResource(R.drawable.no_icon);
    }

    public void SendBroadcast(View v) {
        if (etJudul.getText().length() == 0) {
            etJudul.setError("Harap diisi!");
        } else if (etIsi.getText().length() == 0) {
            etIsi.setError("Harap diisi!");
        } else if (imgklik == 1){
            BitmapDrawable drawable = (BitmapDrawable) imgsend.getDrawable();
            myBitmap = drawable.getBitmap();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
            byte[] ba = bytes.toByteArray();
            b64 = Base64.encodeToString(ba, Base64.NO_WRAP);

            Send();

        } else {
            b64 = "";
            Send();
        }
    }

    public void Send() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("bc_title", etJudul.getText().toString());
        requestParams.put("bc_message", etIsi.getText().toString());
        requestParams.put("bc_image", b64);
        requestParams.put("bc_type", typenotif);
        requestParams.put("bc_to", sKirimke);
        requestParams.put("user_utc", sUTC);
        StringEntity entity = null;

        try {
            entity = new StringEntity(requestParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LoopjHttpClient.post(this, Constant.Send_Broadcast + sToken, entity, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                loadView.setVisibility(View.VISIBLE);
                rlView.setVisibility(View.GONE);
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
                    loadView.setVisibility(View.GONE);
                    onBackPressed();
                    Toast.makeText(AddBroadcastActivity.this, "Terkirim", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddBroadcastActivity.this, "Coba lagi.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "onFailure: " + responseString );
                loadView.setVisibility(View.GONE);
                rlView.setVisibility(View.VISIBLE);
                Toast.makeText(AddBroadcastActivity.this, "Koneksi Anda bermasalah silahkan coba lagi.", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "onFailure: " + throwable.toString());
                Log.i(TAG, "onFailure: Muncul dong");
                loadView.setVisibility(View.GONE);
                rlView.setVisibility(View.VISIBLE);
                Toast.makeText(AddBroadcastActivity.this, "Koneksi Anda bermasalah silahkan coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });
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
}
