package com.huji.apps.haji.petugas;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailBroadcastActivity extends AppCompatActivity {

    ImageView imageViewP, imgIsi;
    TextView txtJudul, txtIsi;
    TextView txtNama, txtWaktu;
    String getBcNama, getBcWaktu, getBcImage, getBcJudul, getBcIsi, getBcPimage,getimgfull;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_broadcast);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Detail Broadcast");

        imageViewP = (ImageView) findViewById(R.id.iconsPetB);
        txtNama = (TextView) findViewById(R.id.tvNamaPetBc);
        txtWaktu = (TextView) findViewById(R.id.tvMenBc);
        txtJudul = (TextView) findViewById(R.id.tvJudBc);
        txtIsi = (TextView) findViewById(R.id.tvIsiBcD);
        imgIsi = (ImageView) findViewById(R.id.imageViewDetB);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if (extras.getString("bcpimg") != null) {
                getBcPimage = extras.getString("bcpimg");
                getBcNama = extras.getString("bcnama");
                getBcWaktu = extras.getString("bcwaktu");
                getBcJudul = extras.getString("bcjudul");
                getBcIsi = extras.getString("bcisi");
                getBcImage = extras.getString("bcimg");
                getimgfull = extras.getString("full");
            }
        }

        txtNama.setText(getBcNama);
        txtWaktu.setText(getBcWaktu);
        txtJudul.setText(getBcJudul);
        txtIsi.setText(getBcIsi);


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
                final Dialog dialog = new Dialog(DetailBroadcastActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.fullimg);
                dialog.setCancelable(true);
                dialog.show();
                ImageView zoom = (ImageView) dialog.findViewById(R.id.imgzoom);
                Picasso.with(DetailBroadcastActivity.this)
                        .load(getimgfull)
                        .placeholder(R.drawable.lod)
                        .into(zoom);
//                zoom.setImageDrawable(imgIsi.getDrawable());
                zoom.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();

                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

