package com.project.presentme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.presentme.R;
import com.project.presentme.utils.Distance;

import java.util.ArrayList;

/**
 * Created by divyang on 9/1/18.
 */

public class HistoryAdapter extends BaseAdapter {

    private ArrayList<Distance> distances;
    private LayoutInflater inflater;

    public HistoryAdapter(Context context, ArrayList<Distance> distances) {
        inflater = LayoutInflater.from(context);
        this.distances = distances;
    }

    @Override
    public int getCount() {
        return distances.size();
    }

    @Override
    public Object getItem(int i) {
        return distances.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        view = inflater.inflate(R.layout.row_history, viewGroup, false);

        viewHolder.tvDateTime = view.findViewById(R.id.tvDateTime);
        viewHolder.tvValue = view.findViewById(R.id.tvValue);

        viewHolder.tvDateTime.setText(distances.get(i).getDistDateTime());
        viewHolder.tvValue.setText(distances.get(i).getDistValue());
        return view;
    }

    public class ViewHolder {
        TextView tvDateTime, tvValue;
    }
}
