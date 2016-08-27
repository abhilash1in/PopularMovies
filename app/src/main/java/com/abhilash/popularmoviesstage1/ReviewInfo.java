package com.abhilash.popularmoviesstage1;

/**
 * Created by Abhilash on 21/08/2016.
 */
public class ReviewInfo {
    String author, content, url;

    public ReviewInfo(String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
