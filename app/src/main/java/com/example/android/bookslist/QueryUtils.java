package com.example.android.bookslist;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;



public final class QueryUtils {

    private QueryUtils(){

    }

    public static List<Book> fetchNewsData(String requestUrl){
        URL url = createUrl(requestUrl);

        String jsonResponse = "";
        try{
            jsonResponse = makeHttpRequest(url);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        List<Book> books = extractFeatureFromJson(jsonResponse);

        return books;
    }


    private static URL createUrl(String url) {
        URL mUrl = null;
        try{
            mUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return mUrl;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if (urlConnection!=null){
                urlConnection.disconnect();

            }if (inputStream!=null){
                inputStream.close();
            }

        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream!=null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line!=null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Book> extractFeatureFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }
        List<Book> books = new ArrayList<Book>();
        String title;
        String author;
        String publishedDate;
        String webReaderLink;
        try {

            JSONObject root = new JSONObject(jsonResponse);
            JSONArray resultsArray = root.getJSONArray("items");
            for (int i = 0; i < resultsArray.length(); i++){
                JSONObject book = resultsArray.getJSONObject(i);
                JSONObject bookResult = book.getJSONObject("volumeInfo");
                if(bookResult.has("title")){
                    title = bookResult.getString("title");
                }
                else{
                    title = "Unknown";
                }
                if (bookResult.has("authors")) {
                    JSONArray authorArray = bookResult.getJSONArray("authors");
                    author = authorArray.getString(0);
                    for (int j = 1; j < authorArray.length(); j++) {
                        author += ", " + authorArray.getString(j);
                    }
                }
                else{
                    author = "Unknown";
                }
                if(bookResult.has("publishedDate")){
                    publishedDate = bookResult.getString("publishedDate");
                }
                else{
                    publishedDate = "Unknown";
                }
                JSONObject accessInfo = book.getJSONObject("accessInfo");
                if(accessInfo.has("webReaderLink")){
                    webReaderLink = accessInfo.getString("webReaderLink");
                }
                else{
                    webReaderLink = null;
                }

                Book mBook = new Book(title,author,publishedDate,webReaderLink);

                books.add(mBook);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return books;
    }


}