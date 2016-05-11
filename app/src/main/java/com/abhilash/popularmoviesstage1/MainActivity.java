package com.abhilash.popularmoviesstage1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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

public class MainActivity extends AppCompatActivity {
    GridView gridView;
    GridAdapter adapter;
    static ArrayList<MovieInfo> data;
    static boolean topRated = false;
    OkHttpClient client;
    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridView);
        client = new OkHttpClient();
        buildRequest();
    }
    private void buildRequest()
    {
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
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONArray resultArray;
                JSONObject responseObject;
                try{
                    responseObject = new JSONObject(response.body().string());
                    resultArray = responseObject.getJSONArray("results");
                    data = new ArrayList<>();
                    for(int i = 0; i<resultArray.length();i++){
                        JSONObject obj = resultArray.getJSONObject(i);
                        data.add(new MovieInfo(obj));
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Handle UI here
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

    private void setupGridView(ArrayList<MovieInfo> listData){
        adapter = new GridAdapter(this, R.layout.grid_item,listData);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.top_rated:
                topRated = true;
                buildRequest();
                break;
            case R.id.popular:
                topRated = false;
                buildRequest();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
