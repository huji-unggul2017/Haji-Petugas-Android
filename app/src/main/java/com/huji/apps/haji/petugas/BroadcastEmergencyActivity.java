package com.huji.apps.haji.petugas;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BroadcastEmergencyActivity extends AppCompatActivity {

    private static final String TAG = "HelpActivity";
    int dot = 200; // Length of a Morse Code "dot" in milliseconds
    int short_gap = 0; // Length of Gap Between dots/dashes
    long[] pattern = {0, // Start immediately
            dot, short_gap, dot, short_gap, dot, // s
            short_gap, short_gap,  // o
            dot, short_gap, dot, short_gap, dot, // s
    };
    ImageView imageViewP, imgIsi;
    TextView txtJudul, txtIsi, txtHead;
    TextView txtNama, txtWaktu;
    String getBcNama, getBcWaktu, getBcImage, getBcJudul, getBcIsi, getBcPimage, isi;
    private Vibrator vib;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_emergency);

        imageViewP = (ImageView) findViewById(R.id.iconsPetB);
        txtNama = (TextView) findViewById(R.id.tvNamaPetBc);
        txtWaktu = (TextView) findViewById(R.id.tvMenBc);
        txtJudul = (TextView) findViewById(R.id.tvJudBc);
        txtIsi = (TextView) findViewById(R.id.tvIsiBcD);
        txtHead = (TextView) findViewById(R.id.tvhead);
        imgIsi = (ImageView) findViewById(R.id.imageViewDetB);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("bcpimg") != null) {
                getBcPimage = extras.getString("bcpimg");
                getBcNama = extras.getString("bcnama");
                getBcWaktu = extras.getString("bcwaktu");
                getBcJudul = extras.getString("bcjudul");
                getBcIsi = extras.getString("bcisi");
                getBcImage = extras.getString("bcimg");
            }
        }
        Log.w(TAG, "onCreate: BCI " + getBcIsi);
        isi = getBcIsi.replaceAll("\\n", "\n");
        Log.w(TAG, "onCreate: ISI " + isi);
        txtNama.setText(getBcNama);
        txtWaktu.setText(getBcWaktu);
        txtJudul.setText(getBcJudul);
        txtIsi.setText(Html.fromHtml(isi));

        txtHead.setPaintFlags(txtHead.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (getBcImage.equals("")) {
            Log.e("TAG", "getView: kos isi");
            imgIsi.setVisibility(View.GONE);
        } else {
            imgIsi.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(getBcImage)
                    .placeholder(R.drawable.lod)
                    .into(imgIsi);
        }

        if (getBcPimage.equals("")) {
            Log.e("TAG", "getView: kos");
        } else {
            Picasso.with(this)
                    .load(getBcPimage)
                    .placeholder(R.drawable.profilee)
                    .into(imageViewP);
        }

        imgIsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(BroadcastEmergencyActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.fullimg);
                dialog.setCancelable(true);
                dialog.show();
                ImageView zoom = (ImageView) dialog.findViewById(R.id.imgzoom);
                zoom.setImageDrawable(imgIsi.getDrawable());
                zoom.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();

                    }
                });
            }
        });

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mp = MediaPlayer.create(this, defaultSoundUri);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        try {
            vib.vibrate(pattern, 0);
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DoneRead(View v) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
