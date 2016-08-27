package com.abhilash.popularmoviesstage1;

/**
 * Created by Abhilash on 21/08/2016.
 */
public class TrailerInfo{
    private String name,key;

    public TrailerInfo(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
