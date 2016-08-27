package com.abhilash.popularmoviesstage1.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.abhilash.popularmoviesstage1.Activities.FavouritesActivity;
import com.abhilash.popularmoviesstage1.Adapters.GridAdapter;
import com.abhilash.popularmoviesstage1.MovieInfo;
import com.abhilash.popularmoviesstage1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GridFragment extends Fragment {
    GridView gridView;
    GridAdapter adapter;
    public static ArrayList<MovieInfo> data;
    OkHttpClient client;
    Request request;
    int width,height;
    android.support.v7.app.ActionBar ab;
    private MovieSelectedListener listener;

    public GridFragment() {
        // Required empty public constructor
    }

    public static GridFragment newInstance() {
        GridFragment gridFragment = new GridFragment();
        return gridFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v("test","onAttach called");
        if(context instanceof MovieSelectedListener){
            listener = (MovieSelectedListener) context;
        }else{
            throw new ClassCastException(context.toString()
                    + " must implement GridFragment.MovieSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("test","onDetach called");
        listener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new OkHttpClient();
        setHasOptionsMenu(true);
        Log.v("test","onCreate called");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.top_rated:
                buildRequest(true);
                break;
            case R.id.popular:
                buildRequest(false);
                break;
            case R.id.favourites:
                Intent intent = new Intent(getActivity(), FavouritesActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("test","onCreateView called");
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_grid, container, false);
        gridView = (GridView) v.findViewById(R.id.gridView);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("data",data);
        Log.v("test","onSaveInstanceState called");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("test","onActivityCreated called");
        try{
            ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        }catch (Exception e){
            e.printStackTrace();
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        Log.v("size"," Width = "+width+", Height = "+height);
        if(savedInstanceState == null){
            buildRequest(false);
            Log.v("test","savedInstanceState == null");
        }else{
            Log.v("test","savedInstanceState != null");
            data = savedInstanceState.getParcelableArrayList("data");
            setupGridView(data);
        }
    }

    private void buildRequest(final boolean topRated)
    {

        final ProgressDialog loading = new ProgressDialog(getActivity());
        loading.setMessage("Fetching movies...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if(!loading.isShowing())
            loading.show();

        if(topRated)
        {
            request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/top_rated?api_key="+getString(R.string.API_key))
                    .get()
                    .build();
        }
        else
        {
            request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/popular?api_key="+getString(R.string.API_key))
                    .get()
                    .build();
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(loading.isShowing())
                            loading.dismiss();
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
                                .setMessage(getResources().getString(R.string.no_internet))
                                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(getActivity(),FavouritesActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().finish();
                                    }
                                });
                        alert.show();
                    }
                });

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(ab != null){
                            if(topRated){
                                ab.setTitle(getResources().getString(R.string.movies_top_rated));
                            }
                            else{
                                ab.setTitle(getResources().getString(R.string.movies_popular));
                            }
                        }
                    }
                });


                JSONArray resultArray;
                JSONObject responseObject;
                try{
                    responseObject = new JSONObject(response.body().string());
                    resultArray = responseObject.getJSONArray("results");
                    data = new ArrayList<>();
                    for(int i = 0; i<resultArray.length();i++){
                        JSONObject obj = resultArray.getJSONObject(i);
                        data.add(new MovieInfo(width/3,obj.toString()));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Handle UI here
                            if(loading.isShowing())
                                loading.dismiss();
                            setupGridView(data);
                        }
                    });
                }
                catch (JSONException je){
                    je.printStackTrace();
                }
            }

        });
    }

    private void setupGridView(final ArrayList<MovieInfo> listData){
        adapter = new GridAdapter(getActivity(), R.layout.grid_item,listData);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.displayDetailsOf(listData.get(position));
            }
        });
    }

    public interface MovieSelectedListener {
        void displayDetailsOf(MovieInfo movieInfo);
    }
}
