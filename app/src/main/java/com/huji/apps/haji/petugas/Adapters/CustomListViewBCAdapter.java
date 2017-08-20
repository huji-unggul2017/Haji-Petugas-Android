package com.huji.apps.haji.petugas.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huji.apps.haji.petugas.Beans.RowItemBc;
import com.huji.apps.haji.petugas.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Cleva 10 on 29/06/2016.
 */
public class CustomListViewBCAdapter extends ArrayAdapter<RowItemBc> {
    customButtonListener customListner;

    public interface customButtonListener {
        void onButtonClickListener(int position, String value);
        void onButtonClickUnListener(int position, String value);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    Context context;
    public CustomListViewBCAdapter(Context context, int resourceId,
                                   List<RowItemBc> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        final RowItemBc rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_broadcast, parent, false);
//            holder = new ViewHolder();
            holder.imageViewP = (ImageView) convertView.findViewById(R.id.iconsp);
            holder.txtNama = (TextView) convertView.findViewById(R.id.tvibcnama);
            holder.txtWaktu = (TextView) convertView.findViewById(R.id.tvibcwaktu);
            holder.txtJudul = (TextView) convertView.findViewById(R.id.tvibcjudul);
            holder.txtIsi = (TextView) convertView.findViewById(R.id.tvibcisi);
            holder.imgIsi = (ImageView) convertView.findViewById(R.id.imgibc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtNama.setText(rowItem.getBcNama());
        holder.txtWaktu.setText(rowItem.getBcWaktu());
        holder.txtJudul.setText(rowItem.getBcJudul());
        holder.txtIsi.setText(rowItem.getBcIsi());


        if (rowItem.getBcImage().equals("")) {
            Log.e("TAG", "getView: "+rowItem.getBcWaktu()+" kos isi" + String.valueOf(position) );
            holder.imgIsi.setVisibility(View.GONE);
        } else {
            holder.imgIsi.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(rowItem.getBcImage())
                    .placeholder(R.drawable.lod)
                    .into(holder.imgIsi);
        }

        if (rowItem.getBcPimage().equals("")) {
            Log.e("TAG", "getView: kos" + String.valueOf(position) );
        } else {
            Picasso.with(context)
                    .load(rowItem.getBcPimage())
                    .placeholder(R.drawable.profilee)
                    .into(holder.imageViewP);
        }
        return convertView;
    }

    /*private view holder class*/
    static class ViewHolder {
        ImageView imageViewP, imgIsi;
        TextView txtJudul, txtIsi;
        TextView txtNama, txtWaktu;
    }
}
