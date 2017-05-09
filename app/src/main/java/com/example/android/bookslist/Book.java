package com.example.android.bookslist;


import android.graphics.Bitmap;

public class Book {

    private Bitmap mImage;

    private String mTitle;

    private String mAuthor;

    private String mPublishedDate;

    private String mUrl;

    public Book(Bitmap image, String title, String author, String publishedDate, String url) {

        mImage = image;
        mTitle = title;
        mAuthor = author;
        mPublishedDate = publishedDate;
        mUrl = url;
    }

    public Bitmap getmImage() {
        return mImage;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmPublishedDate() {
        return mPublishedDate;
    }

    public String getmUrl() {
        return mUrl;
    }
}
