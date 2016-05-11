package com.abhilash.popularmoviesstage1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Abhilash on 11/05/2016.
 */
public class GridAdapter extends ArrayAdapter<MovieInfo> {

    Context context;

    public GridAdapter(Context context, int resource, ArrayList<MovieInfo> objects) {
        super(context, resource, objects);
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.poster);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        MovieInfo info = getItem(position);
        Picasso.with(context).load(info.getUrlForPoster()).into(holder.image);
        return convertView;
    }

    static class ViewHolder {
        ImageView image;
    }
}
