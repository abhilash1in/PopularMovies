package com.abhilash.popularmoviesstage1.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.abhilash.popularmoviesstage1.Adapters.ReviewListAdapter;
import com.abhilash.popularmoviesstage1.Adapters.TrailerListAdapter;
import com.abhilash.popularmoviesstage1.MovieInfo;
import com.abhilash.popularmoviesstage1.MySQLiteHelper;
import com.abhilash.popularmoviesstage1.R;
import com.abhilash.popularmoviesstage1.ReviewInfo;
import com.abhilash.popularmoviesstage1.TrailerInfo;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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

public class DetailFragment extends Fragment {
    TextView title, releaseDate, rating, overview;
    ImageView poster;
    android.support.v7.app.ActionBar ab;
    Button reviewsButton, trailersButton;
    MaterialFavoriteButton favoriteButton;
    OkHttpClient client;
    private MovieInfo info;

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TITLE,
            MySQLiteHelper.COLUMN_RELEASE_DATE,
            MySQLiteHelper.COLUMN_USER_RATING,
            MySQLiteHelper.COLUMN_OVERVIEW,
            MySQLiteHelper.COLUMN_POSTER_URL,
            MySQLiteHelper.COLUMN_BACKDROP_URL
    };

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addFavourite(MovieInfo favourite){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ID, favourite.getId());
        values.put(MySQLiteHelper.COLUMN_TITLE, favourite.getTitle());
        values.put(MySQLiteHelper.COLUMN_RELEASE_DATE, favourite.getReleaseDate());
        values.put(MySQLiteHelper.COLUMN_USER_RATING, favourite.getUserRating());
        values.put(MySQLiteHelper.COLUMN_OVERVIEW, favourite.getOverview());
        values.put(MySQLiteHelper.COLUMN_POSTER_URL, favourite.getUrlForPoster());
        values.put(MySQLiteHelper.COLUMN_BACKDROP_URL, favourite.getUrlForBackdrop());
        database.insert(MySQLiteHelper.TABLE_FAVOURITES, null, values);
    }

    public void deleteFavourite(MovieInfo favourite){
        long id = favourite.getId();
        database.delete(MySQLiteHelper.TABLE_FAVOURITES, MySQLiteHelper.COLUMN_ID + " = " + id, null);
        System.out.println("Comment deleted with id: " + id);
    }

    public boolean isFavourite(MovieInfo movie){
        Cursor cursor=database.query(MySQLiteHelper.TABLE_FAVOURITES, allColumns, MySQLiteHelper.COLUMN_ID+" = "+movie.getId(), null, null, null, null);
        if(cursor != null ){
            cursor.moveToLast();
            int count=cursor.getCount();
            cursor.close();
            if(count == 0)
            {
                //movie not present in favourites
                return false;
            }
            else
            {
                //movie present in favourites
                return true;
            }
        }
        else{
            return false;
        }
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(MovieInfo info) {
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.info = info;
        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new OkHttpClient();
        Log.v("test","onCreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        reviewsButton = (Button) v.findViewById(R.id.reviewsButton);
        trailersButton = (Button) v.findViewById(R.id.trailersButton);
        favoriteButton = (MaterialFavoriteButton) v.findViewById(R.id.favouriteButton);

        title = (TextView) v.findViewById(R.id.titleDetail);
        releaseDate = (TextView) v.findViewById(R.id.releaseYearDetail);
        rating = (TextView) v.findViewById(R.id.userRatingDetail);
        overview = (TextView) v.findViewById(R.id.overviewDetail);
        poster = (ImageView) v.findViewById(R.id.posterDetail);

        Log.v("test","onCreateView called");
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            info = savedInstanceState.getParcelable("movie");
        }
        super.onActivityCreated(savedInstanceState);
        try{
            ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        }catch (Exception e){
            e.printStackTrace();
        }
        dbHelper = new MySQLiteHelper(getActivity());
        open();

        trailersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog loading = new ProgressDialog(getActivity());
                loading.setMessage(getResources().getString(R.string.loading));
                loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                loading.show();
                Request request = new Request.Builder()
                        .url("https://api.themoviedb.org/3/movie/"+info.getId()+"/videos?api_key=312dc944ac0680b1dacf04de89abe1a4")
                        .get()
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(loading.isShowing())
                                    loading.dismiss();
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
                                        .setMessage(getResources().getString(R.string.check_internet))
                                        .setPositiveButton(getResources().getString(R.string.ok), null);
                                alert.show();
                            }
                        });
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        JSONArray resultArray;
                        JSONObject responseObject;
                        final ArrayList<TrailerInfo> trailers = new ArrayList<>();
                        try{
                            responseObject = new JSONObject(response.body().string());
                            resultArray = responseObject.getJSONArray("results");
                            for(int i = 0; i < resultArray.length();i++){
                                JSONObject obj = resultArray.getJSONObject(i);
                                trailers.add(new TrailerInfo(obj.getString("name"),obj.getString("key")));
                            }
                        }
                        catch (JSONException je){
                            je.printStackTrace();
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(loading.isShowing())
                                    loading.dismiss();

                                final Dialog dialog = new Dialog(getActivity());

                                View view = getActivity().getLayoutInflater().inflate(R.layout.listview_layout, null);

                                ListView lv = (ListView) view.findViewById(R.id.listview);
                                TrailerListAdapter adapter = new TrailerListAdapter(getActivity(), trailers);
                                lv.setAdapter(adapter);
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String youtubeUrl = "https://www.youtube.com/watch?v="+trailers.get(position).getKey();
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                                        startActivity(intent);
                                    }
                                });
                                dialog.setContentView(view);
                                dialog.show();
                            }
                        });
                    }
                });
            }
        });


        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog loading = new ProgressDialog(getActivity());
                loading.setMessage(getResources().getString(R.string.loading));
                loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                loading.show();


                final Request request = new Request.Builder()
                        .url("https://api.themoviedb.org/3/movie/"+info.getId()+"/reviews?api_key=312dc944ac0680b1dacf04de89abe1a4")
                        .get()
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(loading.isShowing())
                                    loading.dismiss();
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
                                        .setMessage(getResources().getString(R.string.check_internet))
                                        .setPositiveButton(getResources().getString(R.string.ok), null);
                                alert.show();
                            }
                        });
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        JSONArray resultArray;
                        JSONObject responseObject;
                        final ArrayList<ReviewInfo> reviews = new ArrayList<>();
                        try{
                            responseObject = new JSONObject(response.body().string());
                            resultArray = responseObject.getJSONArray("results");
                            for(int i = 0; i < resultArray.length();i++){
                                JSONObject obj = resultArray.getJSONObject(i);
                                reviews.add(new ReviewInfo(obj.getString("author"),
                                        obj.getString("content"),
                                        obj.getString("url")));
                            }
                        }
                        catch (JSONException je){
                            je.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(loading.isShowing())
                                    loading.dismiss();
                                if(reviews.size() == 0){
                                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
                                            .setMessage(getResources().getString(R.string.no_reviews))
                                            .setPositiveButton(getResources().getString(R.string.ok),null);
                                    alert.show();
                                }else{
                                    final Dialog dialog = new Dialog(getActivity());

                                    View view = getActivity().getLayoutInflater().inflate(R.layout.listview_layout, null);

                                    ListView lv = (ListView) view.findViewById(R.id.listview);
                                    ReviewListAdapter adapter = new ReviewListAdapter(getActivity(), reviews);
                                    lv.setAdapter(adapter);
                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviews.get(position).getUrl()));
                                            startActivity(intent);
                                        }
                                    });
                                    dialog.setContentView(view);
                                    dialog.show();
                                }
                            }
                        });
                    }
                });
            }
        });

        favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                if(favorite && !isFavourite(info)){
                    addFavourite(info);
                }else if (!favorite && isFavourite(info)){
                    deleteFavourite(info);
                }
            }
        });
        updateViews();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("movie",info);
    }



    @Override
    public void onPause() {
        super.onPause();
        close();
    }


    private String getReleaseDate(String d){
        String date[] = d.split("-");
        String year = date[0];
        String month;
        switch (date[1]){
            case "01":
                month = "January";
                break;
            case "02":
                month = "February";
                break;
            case "03":
                month = "March";
                break;
            case "04":
                month = "April";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "June";
                break;
            case "07":
                month = "July";
                break;
            case "08":
                month = "August";
                break;
            case "09":
                month = "September";
                break;
            case "10":
                month = "October";
                break;
            case "11":
                month = "November";
                break;
            case "12":
                month = "December";
                break;
            default:
                month = "";
        }
        return month+" "+year;
    }

    private void updateViews()
    {
        Log.v("test","updateViews called");
        favoriteButton.setFavorite(isFavourite(info));
        title.setText(info.getTitle());
        ab.setTitle(info.getTitle());
        releaseDate.setText(getReleaseDate(info.getReleaseDate()));
        rating.setText(info.getUserRating());
        overview.setText(info.getOverview());
        Picasso.with(getActivity()).load(info.getUrlForPoster()).into(poster);
        Log.v("test","updateView finished: "+info.getTitle()+" "+info.getId());
    }
}
