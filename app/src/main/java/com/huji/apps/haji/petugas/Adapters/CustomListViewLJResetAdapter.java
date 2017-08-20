package com.huji.apps.haji.petugas.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huji.apps.haji.petugas.Beans.RowItemLJReset;
import com.huji.apps.haji.petugas.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Cleva 10 on 29/06/2016.
 */
public class CustomListViewLJResetAdapter extends ArrayAdapter<RowItemLJReset> {
    private static final String TAG = "CustomListViewLJAdapter";
    customButtonListener customListner;
    Context context;

    public CustomListViewLJResetAdapter(Context context, int resourceId,
                                        List<RowItemLJReset> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    @NonNull
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        final RowItemLJReset rowItem = getItem(position);
        assert rowItem != null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.itemjemaahreset, null);
            holder.imageViewP = (ImageView) convertView.findViewById(R.id.iconsLj);
            holder.txtNama = (TextView) convertView.findViewById(R.id.tvNamaLj);
            holder.btnReset = (Button) convertView.findViewById(R.id.buttonreset);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.btnReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListener(position, rowItem.getsStatus());
                }
            }
        });
        holder.txtNama.setText(rowItem.getsNama());

        Picasso.with(context)
                .load(rowItem.getsImage())
                .placeholder(R.drawable.profilee)
                .into(holder.imageViewP);

        return convertView;
    }

    public interface customButtonListener {
        void onButtonClickListener(int position, String value);

        void onButtonClickUnListener(int position, String value);
    }

    /*private view holder class*/
    private static class ViewHolder {
        ImageView imageViewP;
        TextView txtStatusTrue, txtStatusFalse;
        TextView txtNama;
        Button btnReset;
    }
}
