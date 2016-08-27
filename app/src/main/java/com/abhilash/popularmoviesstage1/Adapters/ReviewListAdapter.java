package com.abhilash.popularmoviesstage1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.abhilash.popularmoviesstage1.R;
import com.abhilash.popularmoviesstage1.ReviewInfo;

import java.util.ArrayList;

/**
 * Created by Abhilash on 21/08/2016.
 */
public class ReviewListAdapter extends ArrayAdapter<ReviewInfo> {
    final Context context;
    final ArrayList<ReviewInfo> reviews;


    public ReviewListAdapter(Context context, ArrayList<ReviewInfo> reviews) {
        super(context, -1, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.review_row, parent, false);
        TextView reviewAuthor = (TextView) rowView.findViewById(R.id.reviewAuthor);
        TextView reviewContent = (TextView) rowView.findViewById(R.id.reviewContent);
        reviewAuthor.setText(reviews.get(position).getAuthor());
        reviewContent.setText(reviews.get(position).getContent());
        return rowView;
    }
}
