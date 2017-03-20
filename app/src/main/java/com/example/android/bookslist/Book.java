package com.example.android.bookslist;



public class Book {

    private String mTitle;

    private String mAuthor;

    private String mPublishedDate;

    private String mUrl;

    public Book(String title, String author, String publishedDate, String url){

        mTitle = title;
        mAuthor = author;
        mPublishedDate = publishedDate;
        mUrl = url;
    }

    public String getmTitle(){
        return mTitle;
    }

    public String getmAuthor(){
        return mAuthor;
    }

    public String getmPublishedDate(){
        return mPublishedDate;
    }

    public String getmUrl(){
        return mUrl;
    }
}
