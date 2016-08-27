package com.abhilash.popularmoviesstage1.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class FavouriteDetailOfflineActivity extends AppCompatActivity {
    TextView title, releaseDate, rating, overview;
    ImageView poster;
    long id;
    android.support.v7.app.ActionBar ab;
    Button reviewsButton, trailersButton;
    MaterialFavoriteButton favoriteButton;

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

    @Override
    protected void onPause() {
        super.onPause();
        close();
    }

    public boolean isFavourite(MovieInfo movie){
        Cursor cursor=database.query(MySQLiteHelper.TABLE_FAVOURITES, allColumns, MySQLiteHelper.COLUMN_ID+" = "+movie.getId(), null, null, null, null);
        if(cursor != null ){
            cursor.moveToLast();
            int count=cursor.getCount();
            cursor.close();
            if(count==0)
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

    public MovieInfo getFavouriteMovie(long id){
        Cursor cursor=database.query(MySQLiteHelper.TABLE_FAVOURITES, allColumns, MySQLiteHelper.COLUMN_ID+" = "+id, null, null, null, null);
        if(cursor.getCount() > 0){
            cursor.moveToPosition(0);
            MovieInfo movie =  new MovieInfo(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),
                    cursor.getString(4),cursor.getString(5),cursor.getString(6));
            cursor.close();
            return movie;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);
        ab = getSupportActionBar();
        final OkHttpClient client = new OkHttpClient();

        title = (TextView) findViewById(R.id.titleDetail);
        releaseDate = (TextView) findViewById(R.id.releaseYearDetail);
        rating = (TextView) findViewById(R.id.userRatingDetail);
        overview = (TextView) findViewById(R.id.overviewDetail);
        poster = (ImageView) findViewById(R.id.posterDetail);
        reviewsButton = (Button) findViewById(R.id.reviewsButton);
        trailersButton = (Button) findViewById(R.id.trailersButton);
        favoriteButton = (MaterialFavoriteButton) findViewById(R.id.favouriteButton);

        dbHelper = new MySQLiteHelper(this);
        open();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getLong("id");
            final MovieInfo movie = getFavouriteMovie(id);
            trailersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog loading = new ProgressDialog(FavouriteDetailOfflineActivity.this);
                    loading.setMessage(getResources().getString(R.string.loading));
                    loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    loading.show();

                    Request request = new Request.Builder()
                            .url("https://api.themoviedb.org/3/movie/"+id+"/videos?api_key=312dc944ac0680b1dacf04de89abe1a4")
                            .get()
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            FavouriteDetailOfflineActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(loading.isShowing())
                                        loading.dismiss();
                                    AlertDialog.Builder alert = new AlertDialog.Builder(FavouriteDetailOfflineActivity.this)
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

                            FavouriteDetailOfflineActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(loading.isShowing())
                                        loading.dismiss();

                                    final Dialog dialog = new Dialog(FavouriteDetailOfflineActivity.this);

                                    View view = getLayoutInflater().inflate(R.layout.listview_layout, null);

                                    ListView lv = (ListView) view.findViewById(R.id.listview);
                                    TrailerListAdapter adapter = new TrailerListAdapter(FavouriteDetailOfflineActivity.this, trailers);
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
                    final ProgressDialog loading = new ProgressDialog(FavouriteDetailOfflineActivity.this);
                    loading.setMessage(getResources().getString(R.string.loading));
                    loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    loading.show();


                    final Request request = new Request.Builder()
                            .url("https://api.themoviedb.org/3/movie/"+id+"/reviews?api_key=312dc944ac0680b1dacf04de89abe1a4")
                            .get()
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            FavouriteDetailOfflineActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(loading.isShowing())
                                        loading.dismiss();
                                    AlertDialog.Builder alert = new AlertDialog.Builder(FavouriteDetailOfflineActivity.this)
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
                            FavouriteDetailOfflineActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(loading.isShowing())
                                        loading.dismiss();
                                    if(reviews.size() == 0){
                                        AlertDialog.Builder alert = new AlertDialog.Builder(FavouriteDetailOfflineActivity.this)
                                                .setMessage(getResources().getString(R.string.no_reviews))
                                                .setPositiveButton(getResources().getString(R.string.ok),null);
                                        alert.show();
                                    }else{
                                        final Dialog dialog = new Dialog(FavouriteDetailOfflineActivity.this);

                                        View view = getLayoutInflater().inflate(R.layout.listview_layout, null);

                                        ListView lv = (ListView) view.findViewById(R.id.listview);
                                        ReviewListAdapter adapter = new ReviewListAdapter(FavouriteDetailOfflineActivity.this, reviews);
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
                    if(favorite && !isFavourite(movie)){
                        addFavourite(movie);
                    }else if (!favorite && isFavourite(movie)){
                        deleteFavourite(movie);
                    }
                }
            });

            if(movie != null)
                updateViews(movie);
            else{
                AlertDialog.Builder alert = new AlertDialog.Builder(this)
                        .setMessage(getResources().getString(R.string.error))
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(FavouriteDetailOfflineActivity.this,FavouritesActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        });
                alert.show();
            }
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.error))
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(FavouriteDetailOfflineActivity.this,FavouritesActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    });
            alert.show();
        }
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

    private void updateViews(MovieInfo info)
    {
        favoriteButton.setFavorite(isFavourite(info));
        title.setText(info.getTitle());
        ab.setTitle(info.getTitle());
        releaseDate.setText(getReleaseDate(info.getReleaseDate()));
        rating.setText(info.getUserRating());
        overview.setText(info.getOverview());
        Picasso.with(this).load(info.getUrlForPoster()).into(poster);
    }
}
