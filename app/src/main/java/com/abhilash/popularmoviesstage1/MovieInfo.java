package com.abhilash.popularmoviesstage1;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Abhilash on 11/05/2016.
 */
public class MovieInfo {
    JSONObject obj;
    String title, release_date,posterUrl, backdropUrl, overview, user_rating;

    public MovieInfo(JSONObject obj) {
        this.obj = obj;
        extractPayload();
    }

    private void extractPayload()
    {
        try{
            title = obj.getString("original_title");
            posterUrl = obj.getString("poster_path");
            backdropUrl = obj.getString("backdrop_path");
            overview = obj.getString("overview");
            user_rating = obj.getString("vote_average");
            release_date = obj.getString("release_date");
        }
        catch (JSONException je)
        {
            je.printStackTrace();
        }
    }

    public String getUrlForPoster(){
        return "http://image.tmdb.org/t/p/w185/"+posterUrl;
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
}
