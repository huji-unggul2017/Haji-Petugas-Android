package com.huji.apps.haji.petugas.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.huji.apps.haji.petugas.Beans.RowItemLJ;
import com.huji.apps.haji.petugas.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Cleva 10 on 29/06/2016.
 */
public class CustomListViewLJAdapter extends BaseAdapter implements Filterable {
    private static final String TAG = "CustomListViewLJAdapter";

    Context context;

    public ArrayList<RowItemLJ> employeeArrayList;
    public ArrayList<RowItemLJ> orig;
    public CustomListViewLJAdapter(Context context, int resourceId,
                                   ArrayList<RowItemLJ> items) {
        super();
        this.context = context;
        this.employeeArrayList = items;
    }

    @NonNull
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<RowItemLJ> results = new ArrayList<>();
                if (orig == null)
                    orig = employeeArrayList;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final RowItemLJ g : orig) {
                            if (g.getsNama().toLowerCase()
                                    .contains(constraint.toString())) {
                                results.add(g);
                                Log.e(TAG, "performFiltering: Add" );
                            }

                            Log.e(TAG, "performFiltering: " +g.getsNama() +"|"+constraint.toString());
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                employeeArrayList = (ArrayList<RowItemLJ>) results.values;
                Log.e(TAG, "publishResults: Result " +results.toString() );
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return employeeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return employeeArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
//        final RowItemLJ rowItem = getItem(position);
//        assert rowItem != null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.itemjemaah, null);
            holder.imageViewP = (ImageView) convertView.findViewById(R.id.iconsLj);
            holder.txtNama = (TextView) convertView.findViewById(R.id.tvNamaLj);
            holder.txtStatusFalse = (TextView) convertView.findViewById(R.id.tvStatusLjNot);
            holder.txtStatusTrue = (TextView) convertView.findViewById(R.id.tvStatusLj);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtNama.setText(employeeArrayList.get(position).getsNama());
        if (employeeArrayList.get(position).getsStatus()) {
            holder.txtStatusFalse.setVisibility(View.GONE);
            holder.txtStatusTrue.setVisibility(View.VISIBLE);
            Log.e(TAG, "getView: " + String.valueOf(employeeArrayList.get(position).getsStatus()) + String.valueOf(position));
        } else {
            holder.txtStatusFalse.setVisibility(View.VISIBLE);
            holder.txtStatusTrue.setVisibility(View.GONE);
            Log.e(TAG, "getView: " + String.valueOf(employeeArrayList.get(position).getsStatus()) + String.valueOf(position));
        }

        Picasso.with(context)
                .load(employeeArrayList.get(position).getsImage())
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
    }
}
