package com.abhilash.popularmoviesstage1;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Abhilash on 11/05/2016.
 */
public class MovieInfo implements Parcelable {
    private String json;
    private String title, release_date,posterUrl, backdropUrl, overview, user_rating;
    private long id;
    int width;

    public MovieInfo(int width, String json) throws JSONException{
        this.width = width;
        this.json = json;
        extractPayload();
    }

    public MovieInfo(long id, String title, String release_date, String user_rating, String overview, String posterUrl, String backdropUrl){
        this.id = id;
        this.title = title;
        this.release_date = release_date;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.overview = overview;
        this.user_rating = user_rating;
    }

    private void extractPayload() throws JSONException
    {
        JSONObject obj = new JSONObject(json);
        try{
            title = obj.getString("original_title");
            posterUrl = obj.getString("poster_path");
            backdropUrl = obj.getString("backdrop_path");
            overview = obj.getString("overview");
            user_rating = obj.getString("vote_average");
            release_date = obj.getString("release_date");
            id = obj.getLong("id");
        }
        catch (JSONException je)
        {
            je.printStackTrace();
        }
    }

    public static String getPosterSize(int width) {
        String widthPath;
        if (width <= 92)
            widthPath = "/w92";
        else if (width <= 154)
            widthPath = "/w154";
        else if (width <= 185)
            widthPath = "/w185";
        else if (width <= 342)
            widthPath = "/w342";
        else if (width <= 500)
            widthPath = "/w500";
        else
            widthPath = "/w780";
        Log.v("size","buildPosterUrl: widthPath=" + widthPath);
        return widthPath;
    }

    public String getUrlForPoster(){
        return "http://image.tmdb.org/t/p"+getPosterSize(width)+posterUrl;

    }

    public String getUrlForBackdrop(){
        return "http://image.tmdb.org/t/p/w185/"+backdropUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public String getOverview() {
        return overview;
    }

    public String getUserRating() {
        return user_rating+"/10";
    }

    public long getId() {
        return id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.json);
        dest.writeString(this.title);
        dest.writeString(this.release_date);
        dest.writeString(this.posterUrl);
        dest.writeString(this.backdropUrl);
        dest.writeString(this.overview);
        dest.writeString(this.user_rating);
        dest.writeLong(this.id);
        dest.writeInt(this.width);
    }

    protected MovieInfo(Parcel in) {
        this.json = in.readString();
        this.title = in.readString();
        this.release_date = in.readString();
        this.posterUrl = in.readString();
        this.backdropUrl = in.readString();
        this.overview = in.readString();
        this.user_rating = in.readString();
        this.id = in.readLong();
        this.width = in.readInt();
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR = new Parcelable.Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel source) {
            return new MovieInfo(source);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };
}
