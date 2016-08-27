package com.abhilash.popularmoviesstage1.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.abhilash.popularmoviesstage1.Adapters.GridAdapter;
import com.abhilash.popularmoviesstage1.MovieInfo;
import com.abhilash.popularmoviesstage1.MySQLiteHelper;
import com.abhilash.popularmoviesstage1.R;

import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity {

    GridAdapter adapter;
    GridView gridView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if(ab != null)
            ab.setTitle(getResources().getString(R.string.favourites_activity));
        gridView = (GridView) findViewById(R.id.favouritesGridView);
        dbHelper = new MySQLiteHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ProgressDialog loading = new ProgressDialog(FavouritesActivity.this);
        loading.setMessage(getResources().getString(R.string.loading));
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();
        open();
        ArrayList<MovieInfo> favourites = getFavourites();
        if(loading.isShowing()){
            loading.dismiss();
        }
        if(favourites.size() != 0){
            setupGridView(favourites);
        }else{
            setupGridView(null);
            AlertDialog.Builder alert = new AlertDialog.Builder(FavouritesActivity.this)
                    .setMessage(getResources().getString(R.string.no_favourites))
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(FavouritesActivity.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
            alert.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        close();
    }

    private void setupGridView(final ArrayList<MovieInfo> listData){
        if(listData != null){
            adapter = new GridAdapter(this, R.layout.grid_item,listData);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(FavouritesActivity.this, FavouriteDetailOfflineActivity.class);
                    intent.putExtra("id",listData.get(position).getId());
                    startActivity(intent);
                }
            });
        }else{
            gridView.setAdapter(null);
        }
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ArrayList<MovieInfo> getFavourites(){
        ArrayList<MovieInfo> favourites = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FAVOURITES, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MovieInfo movie = cursorToMovie(cursor);
            favourites.add(movie);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return favourites;
    }

    private MovieInfo cursorToMovie(Cursor cursor) {
        return new MovieInfo(cursor.getLong(0),cursor.getString(1),cursor.getString(2),
                cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
    }
}
