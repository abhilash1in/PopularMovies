package com.abhilash.popularmoviesstage1.Activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.abhilash.popularmoviesstage1.Fragments.DetailFragment;
import com.abhilash.popularmoviesstage1.Fragments.GridFragment;
import com.abhilash.popularmoviesstage1.MovieInfo;
import com.abhilash.popularmoviesstage1.R;



public class MainActivity extends AppCompatActivity implements GridFragment.MovieSelectedListener{

    boolean dualPane;
    FrameLayout gridFrag, detailsFrag;
    android.support.v4.app.FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("test","MainActivity onCreate called");
        try{
            setContentView(R.layout.activity_main);
        }catch (Exception e){
            e.printStackTrace();
        }
        fm = getSupportFragmentManager();
        gridFrag = (FrameLayout) findViewById(R.id.grid_frag);
        detailsFrag = (FrameLayout) findViewById(R.id.details_frag);
        if(savedInstanceState == null){
            Log.v("test","MainActivity savedInstanceState == null");
            getSupportFragmentManager().beginTransaction().replace(R.id.grid_frag,GridFragment.newInstance(),"grid_fragment").commit();
        }else{
            Log.v("test","MainActivity savedInstanceState != null");
            GridFragment gridFragment = (GridFragment) getSupportFragmentManager().findFragmentByTag("grid_fragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.grid_frag,gridFragment,"grid_fragment").commit();
        }
    }

    @Override
    public void displayDetailsOf(MovieInfo movie) {
        dualPane =  (detailsFrag != null && detailsFrag.getVisibility() == View.VISIBLE);
        if(dualPane) {
            Log.v("test","MainActivity dualPane layout drawn");
            DetailFragment detailFragment = DetailFragment.newInstance(movie);
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.details_frag, detailFragment);
            ft.commit();
        }
        else{ /*single pane*/
            Log.v("test","MainActivity singlePane layout drawn");
            Intent intent = new Intent(MainActivity.this,DetailActivity.class);
            intent.putExtra("movie",movie);
            startActivity(intent);
        }
    }
}