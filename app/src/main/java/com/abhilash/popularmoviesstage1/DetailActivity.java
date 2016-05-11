package com.abhilash.popularmoviesstage1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    TextView title, releaseDate, rating, overview;
    ImageView poster;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            position = bundle.getInt("position");
        }

        title = (TextView) findViewById(R.id.titleDetail);
        releaseDate = (TextView) findViewById(R.id.releaseYearDetail);
        rating = (TextView) findViewById(R.id.userRatingDetail);
        overview = (TextView) findViewById(R.id.overviewDetail);
        poster = (ImageView) findViewById(R.id.posterDetail);

        updateViews(position);
    }

    private void updateViews(int position)
    {
        MovieInfo info = MainActivity.data.get(position);
        title.setText(info.getTitle());
        releaseDate.setText(info.getReleaseDate().split("-")[0]);
        rating.setText(info.getUserRating());
        overview.setText(info.getOverview());
        Picasso.with(this).load(info.getUrlForPoster()).into(poster);
    }
}
