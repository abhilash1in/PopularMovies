package com.abhilash.popularmoviesstage1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Abhilash on 22/08/2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_FAVOURITES = "favourites";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_USER_RATING = "user_rating";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final String COLUMN_POSTER_URL = "poster_url";
    public static final String COLUMN_BACKDROP_URL = "backdrop_url";

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table " + TABLE_FAVOURITES + "( "
            + COLUMN_ID+" integer primary key, "
            + COLUMN_TITLE+" text,"
            + COLUMN_RELEASE_DATE+" text,"
            + COLUMN_USER_RATING+" text,"
            + COLUMN_OVERVIEW+" text,"
            + COLUMN_POSTER_URL+" text,"
            + COLUMN_BACKDROP_URL+" text"
            + ");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        onCreate(db);
    }
}
