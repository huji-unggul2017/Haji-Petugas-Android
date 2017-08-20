package com.huji.apps.haji.petugas.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huji.apps.haji.petugas.Beans.RowItemHotel;
import com.huji.apps.haji.petugas.R;

import java.util.List;

/**
 * Created by Cleva 10 on 29/06/2016.
 */
public class CustomListViewHotelAdapter extends ArrayAdapter<RowItemHotel> {
    private static final String TAG = "CustomListViewLJAdapter";
    customButtonListener customListner;
    Context context;

    public CustomListViewHotelAdapter(Context context, int resourceId,
                                      List<RowItemHotel> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    @NonNull
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        final RowItemHotel rowItem = getItem(position);
        assert rowItem != null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.itemhotel, null);
            holder.imageViewP = (ImageView) convertView.findViewById(R.id.iconsLj);
            holder.txtNama = (TextView) convertView.findViewById(R.id.tvNamaLj);
            holder.txtStatusFalse = (TextView) convertView.findViewById(R.id.tvStatusLjNot);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtNama.setText(rowItem.getsPlaceName());
        holder.txtStatusFalse.setText(rowItem.getsPlaceAddress());

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
    }
}
