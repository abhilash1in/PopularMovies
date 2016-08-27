package com.abhilash.popularmoviesstage1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhilash.popularmoviesstage1.R;
import com.abhilash.popularmoviesstage1.TrailerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhilash on 21/08/2016.
 */
public class TrailerListAdapter extends ArrayAdapter<TrailerInfo> {
    final Context context;
    final ArrayList<TrailerInfo> trailers;


    public TrailerListAdapter(Context context, ArrayList<TrailerInfo> trailers) {
        super(context, -1, trailers);
        this.context = context;
        this.trailers = trailers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.trailer_row, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.trailerName);
        textView.setText(trailers.get(position).getName());
        return rowView;
    }
}
