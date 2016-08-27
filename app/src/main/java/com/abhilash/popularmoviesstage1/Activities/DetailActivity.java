package com.abhilash.popularmoviesstage1.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.abhilash.popularmoviesstage1.Fragments.DetailFragment;
import com.abhilash.popularmoviesstage1.MovieInfo;
import com.abhilash.popularmoviesstage1.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MovieInfo movie = (MovieInfo) getIntent().getExtras().getParcelable("movie");
        if(movie != null){
            DetailFragment detailFragment = DetailFragment.newInstance(movie);
            Log.v("test",movie.getTitle()+" "+movie.getId());

            if(savedInstanceState == null){
                Log.v("test","DetailsActivity savedInstanceState == null");
                getSupportFragmentManager().beginTransaction().replace(R.id.details_frag,detailFragment,"detail_fragment").commit();
            }else{
                Log.v("test","DetailsActivity savedInstanceState != null");
                DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag("detail_fragment");
                getSupportFragmentManager().beginTransaction().replace(R.id.details_frag,df,"detail_fragment").commit();
            }
            Log.v("test","fragment replaced");
        }else{
            Log.v("test","received parcelable is null");
        }
    }
}
